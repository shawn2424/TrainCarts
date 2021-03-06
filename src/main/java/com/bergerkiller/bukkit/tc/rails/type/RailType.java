package com.bergerkiller.bukkit.tc.rails.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogic;

public abstract class RailType {
	private static final List<RailType> values = new ArrayList<RailType>();
	public static final RailTypeVertical VERTICAL = new RailTypeVertical();
	public static final RailTypeActivator ACTIVATOR_ON = new RailTypeActivator(true);
	public static final RailTypeActivator ACTIVATOR_OFF = new RailTypeActivator(false);
	public static final RailTypeCrossing CROSSING = new RailTypeCrossing();
	public static final RailTypeRegular REGULAR = new RailTypeRegular();
	public static final RailTypeDetector DETECTOR = new RailTypeDetector();
	public static final RailTypePowered BRAKE = new RailTypePowered(false);
	public static final RailTypePowered BOOST = new RailTypePowered(true);
	public static final RailTypeNone NONE = new RailTypeNone();

	static {
		for (RailType type : CommonUtil.getClassConstants(RailType.class)) {
			if (type != NONE) {
				values.add(type);
			}
		}
	}

	/**
	 * Checks whether the block type Id and data given denote this type of Rail.
	 * 
	 * @param typeId of the Block
	 * @param data of the Block
	 * @return True if it is this type of Rail, False if not
	 */
	public abstract boolean isRail(int typeId, int data);

	/**
	 * Checks whether the Block specified denote this type of Rail
	 * 
	 * @param world the Block is in
	 * @param x - coordinate of the Block
	 * @param y - coordinate of the Block
	 * @param z - coordinate of the Block
	 * @return True if it is this Rail, False if not
	 */
	public boolean isRail(World world, int x, int y, int z) {
		return isRail(WorldUtil.getBlockTypeId(world, x, y, z), WorldUtil.getBlockData(world, x, y, z));
	}

	/**
	 * Checks whether the Block face specified denote this type of Rail
	 * 
	 * @param block to check
	 * @param offset face from the block to check
	 * @return True if it is this Rail, False if not
	 */
	public boolean isRail(Block block, BlockFace offset) {
		return isRail(block.getWorld(), block.getX() + offset.getModX(), block.getY() + offset.getModY(), 
				block.getZ() + offset.getModZ());
	}

	/**
	 * Checks whether the Block specified denote this type of Rail
	 * 
	 * @param block to check
	 * @return True if it is this Rail, False if not
	 */
	public boolean isRail(Block block) {
		return isRail(block.getWorld(), block.getX(), block.getY(), block.getZ());
	}

	/**
	 * Tries to find this Rail Type near the Minecart at the Block position specified.
	 * 
	 * @param member to find the rail for
	 * @param world the Minecart is in
	 * @param pos of the Minecart
	 * @return the Rail position (of this type) the Minecart is on
	 */
	public abstract IntVector3 findRail(MinecartMember<?> member, World world, IntVector3 pos);

	/**
	 * Tries to find this Rail Type near a 'next' Block returned by {@link #getNextPos(Block, BlockFace)}.
	 * Unlike {@link #findRail(MinecartMember, World, IntVector3)} this method
	 * does not allow special adjustments based on Minecart information.
	 * 
	 * @param pos Block a Minecart is 'at'
	 * @return the rail of this type, or null if not found
	 */
	public abstract Block findRail(Block pos);

	/**
	 * Gets the Block where a Minecart would be if it was using this rail.
	 * This is the inverse of {@link #findRail(Block)}.
	 * 
	 * @param trackBlock where this Rail Type is at
	 * @return Minecart position
	 */
	public abstract Block findMinecartPos(Block trackBlock);

	/**
	 * Gets an array containing all possible directions a Minecart can move on the trackBlock.
	 * 
	 * @param trackBlock to use
	 * @return all possible directions the Minecart can move
	 */
	public abstract BlockFace[] getPossibleDirections(Block trackBlock);

	/**
	 * Gets the next Block while moving on this type of Rail.
	 * The goal of this method is to find out where Minecarts that enter this rail
	 * end up at when moving forward.<br><br>
	 * 
	 * If the result is null, then this Rail Type forcibly disallows that direction
	 * from being used, and no movement was possible.
	 * 
	 * @param currentTrack of this rail type the 'Minecart' is using to drive on
	 * @param currentDirection the 'Minecart' is moving
	 * @return next Block to go to after moving over this rail
	 */
	public abstract Block getNextPos(Block currentTrack, BlockFace currentDirection);

	/**
	 * Obtains the direction of this type of Rails.
	 * This is the direction along minecarts move.
	 * 
	 * @param railsBlock to get it for
	 * @return rails Direction
	 */
	public abstract BlockFace getDirection(Block railsBlock);

	public abstract BlockFace getSignColumnDirection(Block railsBlock);

	/**
	 * Obtains the Rail Logic to use for the Minecart at the (previously calculated) rail position in a World.
	 * 
	 * @param member to get the logic for
	 * @param railsBlock the Minecart is driving on
	 * @return Rail Logic
	 */
	public abstract RailLogic getLogic(MinecartMember<?> member, Block railsBlock);

	/**
	 * Handles collision with this Rail Type
	 * 
	 * @param with Minecart that his this Rail
	 * @param block of this Rail
	 * @param hitFace of this Rail
	 * @return True if collision is allowed, False if not
	 */
	public boolean onCollide(MinecartMember<?> with, Block block, BlockFace hitFace) {
		return true;
	}

	/**
	 * Handles a Minecart colliding with a Block while using this Rail Type.
	 * 
	 * @param member that collided
	 * @param railsBlock the member is driving on
	 * @param hitBlock the Minecart hit
	 * @param hitFace the Minecart hit
	 * @return True if collision is allowed, False if not
	 */
	public boolean onBlockCollision(MinecartMember<?> member, Block railsBlock, Block hitBlock, BlockFace hitFace) {
		return true;
	}

	/**
	 * Unregisters a Rail Type so it can no longer be used
	 * 
	 * @param type to unregister
	 */
	public static void unregister(RailType type) {
		values.remove(type);
	}

	/**
	 * Registers a Rail Type for use
	 * 
	 * @param type to register
	 * @param withPriority - True to make it activate prior to other types, False after
	 */
	public static void register(RailType type, boolean withPriority) {
		if (withPriority) {
			values.add(0, type);
		} else {
			values.add(type);
		}
	}

	/**
	 * Gets a Collection of all available Rail Types.
	 * The NONE constant is not included.
	 * 
	 * @return Rail Types
	 */
	public static Collection<RailType> values() {
		return values;
	}

	/**
	 * Tries to find the Rail Type a specific rails block represents.
	 * If none is identified, NONE is returned.
	 * 
	 * @param railsBlock to get the RailType of
	 * @return the RailType, or NONE if not found
	 */
	public static RailType getType(Block railsBlock) {
		if (railsBlock != null) {
			for (RailType type : values()) {
				if (type.isRail(railsBlock)) {
					return type;
				}
			}
		}
		return NONE;
	}
}
