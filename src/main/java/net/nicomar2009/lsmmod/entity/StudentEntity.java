package net.nicomar2009.lsmmod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class StudentEntity extends SchoolNpcEntity {
    public StudentEntity(EntityType<? extends StudentEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean isTeacher() { return false; }
}
