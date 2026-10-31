package dev.lopyluna.dndecor.register.helpers.list_providers;

import java.util.function.Supplier;
import net.minecraft.world.level.block.entity.BlockEntityType;

import dev.lopyluna.dndecor.port.registrate.util.entry.BlockEntityEntry;
import dev.lopyluna.dndecor.port.registrate.util.nullness.NonNullSupplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

@SuppressWarnings("unused")
public class StoneTypeBEList<T extends BlockEntity> implements Iterable<Supplier<BlockEntityType<T>>> {
    // PORT-NOTE(26.1): suppliers, not BlockEntityEntry: Create Fly's own andesite types are plain BlockEntityTypes
    private final Supplier<?>[] values;

    public StoneTypeBEList(BiFunction<NonNullSupplier<Block>, String, Supplier<? extends BlockEntityType<? extends T>>> filler) {
        MaterialTypeProvider.checkStoneTypeList();
        values = new Supplier<?>[MaterialTypeProvider.stoneTypes.size()];
        for (var type : MaterialTypeProvider.stoneTypes) {
            int index = MaterialTypeProvider.stoneTypes.indexOf(type);
            var id = MaterialTypeProvider.stoneTypesRegister.get(type);
            if (index >= 0) values[index] = filler.apply(type, id);
        }
    }

    @SuppressWarnings("unchecked")
    public Supplier<BlockEntityType<T>> get(Block block) {
        NonNullSupplier<Block> type = () -> block;
        int index = MaterialTypeProvider.stoneTypes.indexOf(type);
        if (index >= 0) return (Supplier<BlockEntityType<T>>) values[index];
        return null;
    }

    @SuppressWarnings("unchecked")
    public Supplier<BlockEntityType<T>> get(NonNullSupplier<Block> type) {
        int index = MaterialTypeProvider.stoneTypes.indexOf(type);
        if (index >= 0) return (Supplier<BlockEntityType<T>>) values[index];
        return null;
    }

    public boolean contains(Block block) {
        for (Supplier<?> entry : values) if (((BlockEntityType<?>) entry.get()).isValid(block.defaultBlockState())) return true;
        return false;
    }

    @SuppressWarnings("unchecked")
    public Supplier<BlockEntityType<T>>[] toArray() {
        return (Supplier<BlockEntityType<T>>[]) Arrays.copyOf(values, values.length);
    }

    @Override
    public @NotNull Iterator<Supplier<BlockEntityType<T>>> iterator() {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < values.length;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Supplier<BlockEntityType<T>> next() {
                if (!hasNext()) throw new NoSuchElementException();
                return (Supplier<BlockEntityType<T>>) values[index++];
            }
        };
    }
}
