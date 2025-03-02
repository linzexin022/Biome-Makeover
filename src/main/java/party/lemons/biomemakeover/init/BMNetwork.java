package party.lemons.biomemakeover.init;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.util.Identifier;
import party.lemons.biomemakeover.BiomeMakeover;
import party.lemons.biomemakeover.network.*;

public class BMNetwork
{
	public static final Identifier SPAWN_ENTITY = new Identifier(BiomeMakeover.MODID, "spawn_entiity");
	public static final Identifier SPAWN_POLTERGEIGHT_PARTICLE = new Identifier(BiomeMakeover.MODID, "spawn_pgp");
	public static final Identifier SPAWN_LIGHTNING_BOTTLE_PARTICLES = new Identifier(BiomeMakeover.MODID, "spawn_lbp");
	public static final Identifier SPAWN_LIGHTNING_ENTITY_PARTICLES = new Identifier(BiomeMakeover.MODID, "spawn_lep");
	public static final Identifier WITCH_QUESTS = new Identifier(BiomeMakeover.MODID, "witch_quests");
	public static final Identifier CL_COMPLETE_QUEST = new Identifier(BiomeMakeover.MODID, "cl_complete_quest");

	public static void initClient()
	{
		ClientSidePacketRegistry.INSTANCE.register(SPAWN_ENTITY, new S2C_SpawnEntityCustom());
		ClientSidePacketRegistry.INSTANCE.register(SPAWN_POLTERGEIGHT_PARTICLE, new S2C_DoPoltergeightParticle());
		ClientSidePacketRegistry.INSTANCE.register(SPAWN_LIGHTNING_BOTTLE_PARTICLES, new S2C_DoLightningBottleParticles());
		ClientSidePacketRegistry.INSTANCE.register(SPAWN_LIGHTNING_ENTITY_PARTICLES, new S2C_DoLightningEntityParticles());
		ClientSidePacketRegistry.INSTANCE.register(WITCH_QUESTS, new S2C_HandleWitchQuests());
	}

	public static void initCommon()
	{
		ServerSidePacketRegistry.INSTANCE.register(CL_COMPLETE_QUEST, new C2S_HandleCompleteQuest());
	}
}
