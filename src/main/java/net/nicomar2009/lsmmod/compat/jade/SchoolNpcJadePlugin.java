package net.nicomar2009.lsmmod.compat.jade;

import net.nicomar2009.lsmmod.LSMMod;
import net.nicomar2009.lsmmod.entity.SchoolNpcEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/** Jade discovers this plugin only when installed; the main mod never references Jade classes. */
@WailaPlugin(LSMMod.MOD_ID)
public final class SchoolNpcJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(SchoolNpcStatsProvider.INSTANCE, SchoolNpcEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(SchoolNpcStatsProvider.Client.INSTANCE, SchoolNpcEntity.class);
    }
}
