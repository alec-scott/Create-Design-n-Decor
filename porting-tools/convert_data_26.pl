#!/usr/bin/env perl
# Converts D&D's generated 1.21.1 NeoForge data (src/generated/resources/data) to 26.1 / Create Fly formats.
# Datagen is stubbed, so this stands in for the recipe provider. Adapted from Steam 'n' Rails' converter.
#
# Run from the repo root: perl porting-tools/convert_data_26.pl
#
# - recipes: ingredient objects ({"item"}/{"tag"}) -> "id" / "#tag"; Create deploying and splashing to Create Fly's
#   codecs (deploying target/ingredient, splashing single ingredient).
# - recipes and advancements gated on neoforge:mod_loaded dye_depot are deleted (Dye Depot has no 26.1 Fabric build,
#   so their colours never exist; see PORTING-26.1.md).
# - neoforge/data_maps (oxidizables, waxables) are deleted: CopperBlockSet registers them with Fabric instead.
# Loot tables, advancements and tags are already in 1.21 formats that 26.1 still reads.
use strict;
use warnings;
use File::Find;
use File::Path qw(remove_tree);
use JSON::PP;

my $json = JSON::PP->new->utf8->canonical->pretty->indent_length(2)->space_before(0);
my $data = 'src/generated/resources/data';
my %warned;

sub warn_once { my $m = shift; print STDERR "warning: $m\n" unless $warned{$m}++; }

sub read_json {
    my $file = shift;
    open my $in, '<:raw', $file or die "$file: $!";
    local $/;
    my $d = $json->decode(<$in>);
    close $in;
    return $d;
}

sub write_json {
    my ($file, $d) = @_;
    open my $out, '>:raw', $file or die "$file: $!";
    print $out $json->encode($d);
    close $out;
}

sub files_under {
    my $dir = shift;
    my @files;
    find(sub { push @files, $File::Find::name if /\.json$/ }, $dir) if -d $dir;
    return sort @files;
}

sub ingredient {
    my $in = shift;
    return $in unless ref $in;
    if (ref $in eq 'ARRAY') {
        my @alts = map { ingredient($_) } @$in;
        return $alts[0] if @alts == 1;
        return \@alts unless grep { /^#/ } @alts;
        # 26.1 ingredients can't list alternative tags; keep the first
        warn_once("alternative tags reduced to the first: @alts");
        return $alts[0];
    }
    return $in->{item} if exists $in->{item};
    return "#$in->{tag}" if exists $in->{tag};
    die 'unknown ingredient ' . $json->encode($in);
}

sub output {
    my $r = shift;
    return { id => $r } unless ref $r;
    my %out = (id => $r->{item} // $r->{id});
    $out{count} = $r->{count} if ($r->{count} // 1) != 1;
    $out{chance} = $r->{chance} if exists $r->{chance} && $r->{chance} != 1;
    $out{components} = $r->{components} if exists $r->{components};
    return \%out;
}

sub needs_dye_depot {
    my $d = shift;
    return grep { ($_->{modid} // '') eq 'dye_depot' } @{ $d->{'neoforge:conditions'} // [] };
}

sub convert_recipe {
    my $r = shift;
    my $type = $r->{type};
    die "unhandled conditions in $type recipe" if $r->{'neoforge:conditions'};
    my %out = (type => $type);
    $out{category} = $r->{category} if exists $r->{category};
    $out{group} = $r->{group} if exists $r->{group};
    $out{show_notification} = $r->{show_notification} if exists $r->{show_notification};

    if ($type eq 'minecraft:crafting_shaped') {
        $out{pattern} = $r->{pattern};
        $out{key} = { map { $_ => ingredient($r->{key}{$_}) } keys %{ $r->{key} } };
        $out{result} = output($r->{result});
    } elsif ($type eq 'minecraft:crafting_shapeless') {
        $out{ingredients} = [ map { ingredient($_) } @{ $r->{ingredients} } ];
        $out{result} = output($r->{result});
    } elsif ($type eq 'minecraft:stonecutting') {
        $out{ingredient} = ingredient($r->{ingredient});
        $out{result} = output($r->{result});
    } elsif ($type =~ /^minecraft:(smelting|blasting|smoking|campfire_cooking)$/) {
        $out{ingredient} = ingredient($r->{ingredient});
        $out{result} = output($r->{result});
        $out{experience} = $r->{experience} if exists $r->{experience};
        $out{cookingtime} = $r->{cookingtime} if exists $r->{cookingtime};
    } elsif ($type eq 'create:deploying') {
        $out{target} = ingredient($r->{ingredients}[0]);
        $out{ingredient} = ingredient($r->{ingredients}[1]);
        $out{results} = [ map { output($_) } @{ $r->{results} } ];
        $out{keep_held_item} = JSON::PP::true if $r->{keep_held_item};
    } elsif ($type eq 'create:splashing') {
        die 'splashing with several ingredients' if @{ $r->{ingredients} } != 1;
        $out{ingredient} = ingredient($r->{ingredients}[0]);
        $out{results} = [ map { output($_) } @{ $r->{results} } ];
        $out{processing_time} = $r->{processing_time} if exists $r->{processing_time};
    } else {
        die "unsupported recipe type $type";
    }
    return \%out;
}

my %count;
for my $section (qw(recipe advancement)) {
    for my $f (files_under("$data/dndecor/$section")) {
        my $d = read_json($f);
        if (needs_dye_depot($d)) { unlink $f or die "$f: $!"; $count{"dye_depot $section deleted"}++; next; }
        next unless $section eq 'recipe';
        write_json($f, convert_recipe($d));
        $count{recipes}++;
    }
}
if (-d "$data/neoforge") { remove_tree("$data/neoforge"); $count{'neoforge data dirs removed'} = 1; }
print "converted: " . join(', ', map { "$count{$_} $_" } sort keys %count) . "\n";
