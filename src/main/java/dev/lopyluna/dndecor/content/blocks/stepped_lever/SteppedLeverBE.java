package dev.lopyluna.dndecor.content.blocks.stepped_lever;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.zurrtum.create.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class SteppedLeverBE extends SmartBlockEntity implements IHaveGoggleInformation {
    int state = 0;
    int lastChange;
    LerpedFloat clientState = LerpedFloat.linear();

    public SteppedLeverBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    @Override
    protected void read(ValueInput compound, boolean clientPacket) {
        this.state = compound.getIntOr("State", 0);
        this.lastChange = compound.getIntOr("ChangeTimer", 0);
        this.clientState.chase(this.state, 0.2F, LerpedFloat.Chaser.EXP);
        super.read(compound, clientPacket);
    }

    @Override
    protected void write(ValueOutput compound, boolean clientPacket) {
        compound.putInt("State", this.state);
        compound.putInt("ChangeTimer", this.lastChange);
        super.write(compound, clientPacket);
    }

    public void tick() {
        super.tick();
        if (level == null) return;
        if (this.lastChange > 0) {
            --this.lastChange;
            if (this.lastChange == 0) this.updateOutput();
        }
        if (this.level.isClientSide()) this.clientState.tickChaser();
    }

    public void initialize() {
        super.initialize();
    }

    private void updateOutput() {
        assert level != null;
        SteppedLeverBlock.updateNeighbors(getBlockState(), level, worldPosition);
    }

    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState oldState) {
        super.preRemoveSideEffects(pos, oldState);
        if (state != 0 && level != null) SteppedLeverBlock.updateNeighbors(oldState, level, pos);
    }

    public void changeState(boolean back) {
        int prevState = state;
        state += back ? -1 : 1;
        state = Mth.clamp(state, 0, 15);
        if (prevState != state) lastChange = 15;
        sendData();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.translate("tooltip.analogStrength", this.state).forGoggles(tooltip);
        return true;
    }

    public int getState() {
        return this.state;
    }
}
