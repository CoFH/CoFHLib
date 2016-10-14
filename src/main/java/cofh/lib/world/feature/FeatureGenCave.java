package cofh.lib.world.feature;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class FeatureGenCave extends FeatureBase {

	final WorldGenerator worldGen;
	final int count;
	final boolean ceiling;

	public FeatureGenCave(String name, WorldGenerator worldGen, boolean ceiling, int count, GenRestriction biomeRes, boolean regen, GenRestriction dimRes) {

		super(name, biomeRes, regen, dimRes);
		this.worldGen = worldGen;
		this.count = count;
		this.ceiling = ceiling;
	}

	@Override
	protected boolean generateFeature(Random random, int chunkX, int chunkZ, World world) {

		int averageSeaLevel = world.provider.getAverageGroundLevel() + 1;
		int blockX = chunkX * 16;
		int blockZ = chunkZ * 16;

		boolean generated = false;
		for (int i = 0; i < count; i++) {
			int x = blockX + random.nextInt(16);
			int z = blockZ + random.nextInt(16);
			if (!canGenerateInBiome(world, x, z, random)) {
				continue;
			}
			int seaLevel = averageSeaLevel;
			if (seaLevel < 20) {
				seaLevel = world.getHeight(new BlockPos(x, 1, z)).getY();
			}

			int stopY = random.nextInt(1 + seaLevel / 2);
			int y = stopY;
			do {
			} while (!world.isAirBlock(new BlockPos(x, y, z)) && ++y < seaLevel);

			if (y == seaLevel) {
				y = 0;
				do {
				} while (!world.isAirBlock(new BlockPos(x, y, z)) && ++y < stopY);
				if (y == stopY) {
					continue;
				}
			}

			if (ceiling) {
				if (y < stopY) {
					seaLevel = stopY + 1;
				}
				do {
					++y;
				} while (y < seaLevel && world.isAirBlock(new BlockPos(x, y, z)));
				if (y == seaLevel) {
					continue;
				}
				--y;
			} else if (world.isAirBlock(new BlockPos(x, y - 1, z))) {
				--y;
				do {
				} while (world.isAirBlock(new BlockPos(x, y, z)) && y-- > 0);
				if (y == -1) {
					continue;
				}
				++y;
			}

			generated |= worldGen.generate(world, random, new BlockPos(x, y, z));
		}
		return generated;
	}

}
