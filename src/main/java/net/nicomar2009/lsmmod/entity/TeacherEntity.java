package net.nicomar2009.lsmmod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class TeacherEntity extends SchoolNpcEntity {
    public TeacherEntity(EntityType<? extends TeacherEntity> type, Level level) {
        super(type, level);
        enforceTeacherEquipment();
    }

    @Override
    public boolean isTeacher() { return true; }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return SchoolNpcEntity.createAttributes().add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 50.0)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, 7.0);
    }
}
