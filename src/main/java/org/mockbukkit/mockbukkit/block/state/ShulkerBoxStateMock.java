package org.mockbukkit.mockbukkit.block.state;

import org.mockbukkit.mockbukkit.exception.UnimplementedOperationException;
import org.mockbukkit.mockbukkit.inventory.InventoryMock;
import org.mockbukkit.mockbukkit.inventory.ShulkerBoxInventoryMock;
import com.google.common.base.Preconditions;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * Mock implementation of a {@link ShulkerBox}.
 *
 * @see ContainerStateMock
 */
public class ShulkerBoxStateMock extends ContainerStateMock implements ShulkerBox
{

	private final @Nullable DyeColor color;
	private boolean isOpen = false;

	/**
	 * Constructs a new {@link ShulkerBoxStateMock} for the provided {@link Material}.
	 * Only supports materials in {@link Tag#SHULKER_BOXES}
	 *
	 * @param material The material this state is for.
	 */
	public ShulkerBoxStateMock(@NotNull Material material)
	{
		super(material);
		checkType(material, Tag.SHULKER_BOXES);
		this.color = getFromMaterial(material);
	}

	/**
	 * Constructs a new {@link ShulkerBoxStateMock} for the provided {@link Block}.
	 * Only supports materials in {@link Tag#SHULKER_BOXES}
	 *
	 * @param block The block this state is for.
	 */
	protected ShulkerBoxStateMock(@NotNull Block block)
	{
		super(block);
		checkType(block, Tag.SHULKER_BOXES);
		this.color = getFromMaterial(block.getType());
	}

	/**
	 * Constructs a new {@link ShulkerBoxStateMock} by cloning the data from an existing one.
	 *
	 * @param state The state to clone.
	 */
	protected ShulkerBoxStateMock(@NotNull ShulkerBoxStateMock state)
	{
		super(state);
		this.color = state.color;
		this.isOpen = state.isOpen;
	}

	@Nullable
	private DyeColor getFromMaterial(@NotNull Material type)
	{
		Preconditions.checkNotNull(type, "Type cannot be null");
		return switch (type)
		{
			case SHULKER_BOX -> null;
			case WHITE_SHULKER_BOX -> DyeColor.WHITE;
			case ORANGE_SHULKER_BOX -> DyeColor.ORANGE;
			case MAGENTA_SHULKER_BOX -> DyeColor.MAGENTA;
			case LIGHT_BLUE_SHULKER_BOX -> DyeColor.LIGHT_BLUE;
			case YELLOW_SHULKER_BOX -> DyeColor.YELLOW;
			case LIME_SHULKER_BOX -> DyeColor.LIME;
			case PINK_SHULKER_BOX -> DyeColor.PINK;
			case GRAY_SHULKER_BOX -> DyeColor.GRAY;
			case LIGHT_GRAY_SHULKER_BOX -> DyeColor.LIGHT_GRAY;
			case CYAN_SHULKER_BOX -> DyeColor.CYAN;
			case PURPLE_SHULKER_BOX -> DyeColor.PURPLE;
			case BLUE_SHULKER_BOX -> DyeColor.BLUE;
			case BROWN_SHULKER_BOX -> DyeColor.BROWN;
			case GREEN_SHULKER_BOX -> DyeColor.GREEN;
			case RED_SHULKER_BOX -> DyeColor.RED;
			case BLACK_SHULKER_BOX -> DyeColor.BLACK;
			default -> throw new IllegalArgumentException(type.name() + " is not a Shulker Box!");
		};
	}

	@Override
	public void setLootTable(LootTable table)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public LootTable getLootTable()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public void setLootTable(@Nullable LootTable lootTable, long l)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public void setSeed(long seed)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public long getSeed()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public void open()
	{
		isOpen = true;
	}

	@Override
	public void close()
	{
		isOpen = false;
	}

	@Override
	public boolean isOpen()
	{
		return isOpen;
	}

	@Override
	protected @NotNull InventoryMock createInventory()
	{
		return new ShulkerBoxInventoryMock(this);
	}

	@Override
	public @NotNull ShulkerBoxStateMock getSnapshot()
	{
		return new ShulkerBoxStateMock(this);
	}

	@Override
	public @NotNull ShulkerBoxStateMock copy()
	{
		return new ShulkerBoxStateMock(this);
	}

	@NotNull
	@Override
	public DyeColor getColor()
	{
		// Don't ask me why but it seems like calling this on an undyed Shulker box
		// throws a NullPointerException rather than simply returning null.
		if (color == null)
		{
			throw new NullPointerException("This Shulker Box has not been dyed");
		}

		return color;
	}

	@Override
	public boolean isRefillEnabled()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public boolean hasBeenFilled()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public boolean canPlayerLoot(@NotNull UUID player)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public boolean hasPlayerLooted(@NotNull UUID player)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public @Nullable Long getLastLooted(@NotNull UUID player)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public boolean setHasPlayerLooted(@NotNull UUID player, boolean looted)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public boolean hasPendingRefill()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public long getLastFilled()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public long getNextRefill()
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public long setNextRefill(long refillAt)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof ShulkerBoxStateMock that)) return false;
		if (!super.equals(o)) return false;
		return isOpen == that.isOpen && color == that.color;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), color, isOpen);
	}


	@Override
	protected String toStringInternal()
	{
		return super.toStringInternal() +
				", color=" + color +
				", isOpen=" + isOpen;
	}

}
