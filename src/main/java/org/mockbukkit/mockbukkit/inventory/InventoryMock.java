package org.mockbukkit.mockbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.entity.EntityMock;
import org.mockbukkit.mockbukkit.exception.UnimplementedOperationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Mock implementation of an {@link Inventory}.
 */
public class InventoryMock implements Inventory
{

	private static final int MAX_STACK_SIZE = 64;

	private final ItemStack @NotNull [] items;
	private final @Nullable InventoryHolder holder;
	private final @NotNull InventoryType type;

	private final @NotNull List<HumanEntity> viewers = new ArrayList<>();
	private int maxStackSize = MAX_STACK_SIZE;

	private @Nullable Component customTitle;

	/**
	 * Constructs a new {@link InventoryMock} for the given holder, with a specific size and {@link InventoryType}.
	 *
	 * @param holder The holder of the inventory.
	 * @param size   The size of the inventory. Must be 2, or a multiple of 9 between 9 and 54.
	 * @param type   The type of the inventory.
	 */
	public InventoryMock(@Nullable InventoryHolder holder, int size, @NotNull InventoryType type)
	{
		Preconditions.checkArgument(size > 0, "Inventory size has to be > 0");
		Preconditions.checkNotNull(type, "The InventoryType must not be null!");

		this.holder = holder;
		this.type = type;

		items = new ItemStack[size];
	}

	/**
	 * Constructs a new {@link InventoryMock} for the given holder with a specific {@link InventoryType}.
	 * The size will be {@link InventoryType#getDefaultSize()}.
	 *
	 * @param holder The holder of the inventory.
	 * @param type   The type of the inventory.
	 */
	public InventoryMock(@Nullable InventoryHolder holder, @NotNull InventoryType type)
	{
		Preconditions.checkNotNull(type, "The InventoryType must not be null!");

		this.holder = holder;
		this.type = type;

		items = new ItemStack[type.getDefaultSize()];
	}

	protected InventoryMock(InventoryMock inventory)
	{
		this.holder = inventory.getHolder();
		this.type = inventory.getType();
		this.items = new ItemStack[inventory.getSize()];

		setMaxStackSize(inventory.getMaxStackSize());
		setContents(inventory.getContents());
		setCustomTitle(inventory.getCustomTitle());
	}

	/**
	 * Copy constructor. Holder is copied by reference, inventory contents are cloned.
	 * @param other Inventory to copy.
	 */
	public InventoryMock(@NotNull Inventory other)
	{
		this.holder = other.getHolder();
		this.type = other.getType();
		this.items = new ItemStack[other.getSize()];
		this.setContents(other.getContents());
	}

	/**
	 * Asserts that a certain condition is true for all items, even {@code nulls}, in this inventory.
	 *
	 * @param condition The condition to check for.
	 */
	@Deprecated(forRemoval = true)
	public void assertTrueForAll(@NotNull Predicate<ItemStack> condition)
	{
		for (ItemStack item : items)
		{
			assertTrue(condition.test(item));
		}
	}

	/**
	 * Assets that a certain condition is true for all items in this inventory that aren't null.
	 *
	 * @param condition The condition to check for.
	 */
	@Deprecated(forRemoval = true)
	public void assertTrueForNonNulls(@NotNull Predicate<ItemStack> condition)
	{
		assertTrueForAll(itemstack -> itemstack == null || condition.test(itemstack));
	}

	/**
	 * Asserts that a certain condition is true for at least one item in this inventory. It will skip any null items.
	 *
	 * @param condition The condition to check for.
	 */
	@Deprecated(forRemoval = true)
	public void assertTrueForSome(@NotNull Predicate<ItemStack> condition)
	{
		for (ItemStack item : items)
		{
			if (item != null && condition.test(item))
			{
				return;
			}
		}
		fail("Condition was not met for any items");
	}

	/**
	 * Asserts that the inventory contains at least one itemstack that is compatible with the given itemstack.
	 *
	 * @param item The itemstack to compare everything to.
	 */
	@Deprecated(forRemoval = true)
	public void assertContainsAny(@NotNull ItemStack item)
	{
		assertTrueForSome(item::isSimilar);
	}

	/**
	 * Asserts that the inventory contains at least a specific amount of items that are compatible with the given
	 * itemstack.
	 *
	 * @param item   The itemstack to search for.
	 * @param amount The minimum amount of items that one should have.
	 */
	@Deprecated(forRemoval = true)
	public void assertContainsAtLeast(@NotNull ItemStack item, int amount)
	{
		int n = getNumberOfItems(item);
		String message = String.format("Inventory contains only <%d> but expected at least <%d>", n, amount);
		assertTrue(n >= amount, message);
	}

	/**
	 * Get the number of times a certain item is in the inventory.
	 *
	 * @param item The item to check for.
	 * @return The number of times the item is present in this inventory.
	 */
	public int getNumberOfItems(@NotNull ItemStack item)
	{
		int amount = 0;
		for (ItemStack itemstack : items)
		{
			if (itemstack != null && item.isSimilar(itemstack))
			{
				amount += itemstack.getAmount();
			}
		}
		return amount;
	}

	/**
	 * Adds a viewer to this inventory.
	 *
	 * @param viewer The viewer to add.
	 */
	public void addViewer(@NotNull HumanEntity viewer)
	{
		Preconditions.checkNotNull(viewer, "The viewer must not be null!");
		viewers.add(viewer);
	}

	/**
	 * Adds the given viewers to this inventory.
	 *
	 * @param viewers The viewers to add.
	 */
	public void addViewers(@NotNull HumanEntity... viewers)
	{
		addViewers(Arrays.asList(viewers));
	}

	/**
	 * Adds the given viewers to this inventory.
	 *
	 * @param viewers The {@link List} of viewers to add.
	 */
	public void addViewers(@NotNull List<HumanEntity> viewers)
	{
		for (HumanEntity viewer : viewers)
		{
			Preconditions.checkNotNull(viewer, "The viewer must not be null!");
			addViewer(viewer);
		}
	}

	/**
	 * Removes a viewer from this inventory.
	 *
	 * @param viewer The viewer to remove.
	 */
	public void removeViewer(@NotNull HumanEntity viewer)
	{
		Preconditions.checkNotNull(viewer, "The viewer must not be null!");
		viewers.remove(viewer);
	}

	@Override
	public int getSize()
	{
		return items.length;
	}

	@Override
	public ItemStack getItem(int index)
	{
		return items[index];
	}

	@Override
	public void setItem(int index, @Nullable ItemStack item)
	{
		items[index] = item == null ? null : item.clone();
	}

	/**
	 * Adds a single item to the inventory. Returns whatever item it couldn't add.
	 *
	 * @param item The item to add.
	 * @return The remaining stack that couldn't be added. If it's empty it just returns {@code null}.
	 */
	@Nullable
	public ItemStack addItem(@NotNull ItemStack item)
	{
		final int itemMaxStackSize = Math.min(item.getMaxStackSize(), this.maxStackSize);
		item = item.clone();
		for (int i = 0; i < items.length; i++)
		{
			ItemStack oItem = items[i];
			if (oItem == null)
			{
				int toAdd = Math.min(item.getAmount(), itemMaxStackSize);
				items[i] = item.clone();
				items[i].setAmount(toAdd);
				item.setAmount(item.getAmount() - toAdd);
			}
			else
			{
				final int oItemMaxStackSize = Math.min(oItem.getMaxStackSize(), this.maxStackSize);
				if (item.isSimilar(oItem) && oItem.getAmount() < oItemMaxStackSize)
				{
					int toAdd = Math.min(item.getAmount(), oItemMaxStackSize - oItem.getAmount());
					oItem.setAmount(oItem.getAmount() + toAdd);
					item.setAmount(item.getAmount() - toAdd);
				}
			}

			if (item.getAmount() == 0)
			{
				return null;
			}
		}

		return item;
	}

	@Override
	public @NotNull HashMap<Integer, ItemStack> addItem(ItemStack @NotNull ... items) throws IllegalArgumentException
	{
		HashMap<Integer, ItemStack> notSaved = new HashMap<>();
		for (int i = 0; i < items.length; i++)
		{
			ItemStack item = items[i];
			ItemStack left = addItem(item);
			if (left != null)
			{
				notSaved.put(i, left);
			}
		}
		return notSaved;
	}

	@Override
	public ItemStack @NotNull [] getContents()
	{
		return items;
	}

	@Override
	public void setContents(ItemStack @NotNull [] items)
	{
		for (int i = 0; i < getSize(); i++)
		{
			if (i < items.length && items[i] != null)
			{
				this.items[i] = items[i].clone();
			}
			else
			{
				this.items[i] = null;
			}
		}
	}

	@Override
	public @Nullable InventoryHolder getHolder()
	{
		return holder;
	}

	@Override
	public @Nullable InventoryHolder getHolder(boolean useSnapshot)
	{
		//TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public @NotNull ListIterator<ItemStack> iterator()
	{
		List<ItemStack> list = stream().collect(Collectors.toList());
		return list.listIterator();
	}

	public @NotNull Stream<ItemStack> stream()
	{
		return Arrays.stream(items).filter(Objects::nonNull);
	}

	@Override
	public @NotNull InventoryType getType()
	{
		return type;
	}

	@Override
	public int getMaxStackSize()
	{
		return maxStackSize;
	}

	@Override
	public void setMaxStackSize(int size)
	{
		// The following checks aren't done in CraftBukkit, but are a fair sanity check.
		if (size < 1)
		{
			throw new IllegalArgumentException("Max stack size cannot be lower than 1");
		}
		if (size > 127)
		{
			throw new IllegalArgumentException("Stack sizes larger than 127 may get clipped");
		}
		maxStackSize = size;
	}

	@Override
	public @NotNull HashMap<Integer, ItemStack> removeItem(ItemStack... items) throws IllegalArgumentException
	{
		Preconditions.checkNotNull(items, "Items cannot be null");
		HashMap<Integer, ItemStack> leftover = new HashMap<>();

		for (int i = 0; i < items.length; i++)
		{
			ItemStack item = items[i];
			int toDelete = item.getAmount();

			while (toDelete > 0)
			{
				int first = first(item, false);

				// Drat! we don't have this type in the inventory
				if (first == -1)
				{
					item.setAmount(toDelete);
					leftover.put(i, item);
					break;
				}

				ItemStack itemStack = getItem(first);
				int amount = itemStack.getAmount();
				if (amount <= toDelete)
				{
					toDelete -= amount;
					// clear the slot, all used up
					clear(first);
				}
				else
				{
					// split the stack and store
					itemStack.setAmount(amount - toDelete);
					setItem(first, itemStack);
					toDelete = 0;
				}
			}
		}
		return leftover;
	}

	@Override
	public @NotNull HashMap<Integer, ItemStack> removeItemAnySlot(@NotNull ItemStack... items) throws IllegalArgumentException
	{
		//TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public ItemStack @NotNull [] getStorageContents()
	{
		return getContents();
	}

	@Override
	public void setStorageContents(ItemStack @NotNull [] items) throws IllegalArgumentException
	{
		setContents(items);
	}

	@Override
	public boolean contains(@Nullable Material material) throws IllegalArgumentException
	{
		if (material == null)
		{
			throw new IllegalArgumentException("Material cannot be null.");
		}
		for (ItemStack itemStack : this.getContents())
		{
			if (itemStack != null && itemStack.getType() == material)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains(ItemStack item)
	{
		return contains(Objects.requireNonNull(item).getType());
	}

	@Override
	public boolean contains(@Nullable Material material, int amount) throws IllegalArgumentException
	{
		if (material == null)
		{
			throw new IllegalArgumentException("Material cannot be null.");
		}
		return amount < 1 || getNumberOfItems(new ItemStackMock(material)) == amount;
	}

	@Override
	public boolean contains(@NotNull ItemStack item, int amount)
	{
		return getNumberOfItems(item) == amount;
	}

	@Override
	public boolean containsAtLeast(@NotNull ItemStack item, int amount)
	{
		return getNumberOfItems(item) >= amount;
	}

	@Override
	public @NotNull HashMap<Integer, ? extends ItemStack> all(@NotNull Material material) throws IllegalArgumentException
	{
		Preconditions.checkNotNull(material, "Material cannot be null");
		HashMap<Integer, ItemStack> slots = new HashMap<>();

		ItemStack[] items = this.getStorageContents();
		for (int i = 0; i < items.length; i++)
		{
			if (items[i] != null && items[i].getType() == material)
			{
				slots.put(i, items[i]);
			}
		}
		return slots;
	}

	@Override
	public @NotNull HashMap<Integer, ? extends ItemStack> all(@Nullable ItemStack item)
	{
		HashMap<Integer, ItemStack> slots = new HashMap<>();
		if (item != null)
		{
			ItemStack[] contents = this.getStorageContents();
			for (int i = 0; i < contents.length; i++)
			{
				if (item.equals(contents[i]))
				{
					slots.put(i, contents[i]);
				}
			}
		}
		return slots;
	}

	@Override
	public int first(@NotNull Material material) throws IllegalArgumentException
	{
		Preconditions.checkNotNull(material, "Material cannot be null");
		ItemStack[] contents = this.getStorageContents();
		for (int i = 0; i < contents.length; i++)
		{
			if (contents[i] != null && contents[i].getType() == material)
			{
				return i;
			}
		}
		return -1;
	}

	@Override
	public int first(@NotNull ItemStack item)
	{
		if (item == null)
		{
			return -1;
		}
		ItemStack[] contents = this.getStorageContents();
		for (int i = 0; i < contents.length; i++)
		{
			if (contents[i] != null && item.equals(contents[i]))
			{
				return i;
			}
		}
		return -1;
	}

	private int first(@NotNull ItemStack item, boolean withAmount)
	{
		Preconditions.checkNotNull(item, "ItemStack cannot be null");

		ItemStack[] inventory = this.getStorageContents();
		for (int i = 0; i < inventory.length; i++)
		{
			if (inventory[i] == null)
			{
				continue;
			}

			if (withAmount ? item.equals(inventory[i]) : item.isSimilar(inventory[i]))
			{
				return i;
			}
		}
		return -1;
	}

	@Override
	public int firstEmpty()
	{
		for (int i = 0; i < getSize(); i++)
		{
			if (items[i] == null || items[i].getType() == Material.AIR)
			{
				return i;
			}
		}

		return -1;
	}

	@Override
	public void remove(@NotNull Material material) throws IllegalArgumentException
	{
		Preconditions.checkNotNull(material, "Material cannot be null");
		ItemStack[] contents = this.getStorageContents();
		for (int i = 0; i < contents.length; i++)
		{
			if (contents[i] != null && contents[i].getType() == material)
			{
				this.clear(i);
			}
		}
	}

	@Override
	public void remove(@NotNull ItemStack item)
	{
		ItemStack[] contents = this.getStorageContents();
		for (int i = 0; i < contents.length; i++)
		{
			if (contents[i] != null && contents[i].equals(item))
			{
				this.clear(i);
			}
		}
	}

	@Override
	public void clear(int index)
	{
		items[index] = null;
	}

	@Override
	public void clear()
	{
		Arrays.fill(items, null);
	}

	@Override
	public int close()
	{
		int count = this.viewers.size();
		Lists.newArrayList(this.viewers).forEach(HumanEntity::closeInventory);
		return count;
	}

	@Override
	public @NotNull List<HumanEntity> getViewers()
	{
		return this.viewers;
	}

	@Override
	public @NotNull ListIterator<ItemStack> iterator(int index)
	{
		// TODO Auto-generated method stub
		throw new UnimplementedOperationException();
	}

	@Override
	public Location getLocation()
	{
		if (getHolder() instanceof EntityMock entity)
		{
			return entity.getLocation();
		}

		var worlds = Bukkit.getWorlds();
		World world;
		if (worlds.isEmpty())
		{
			world = MockBukkit.getMock().addSimpleWorld("world");
		}
		else
		{
			world = worlds.getFirst();
		}

		return world.getSpawnLocation();
	}

	@Override
	public boolean isEmpty()
	{
		for (int i = 0; i < getSize(); i++)
		{
			if (items[i] != null && items[i].getType() != Material.AIR)
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Creates a snapshot of the inventory.
	 *
	 * @return An inventory snapshot.
	 */
	@NotNull
	public Inventory getSnapshot()
	{
		return new InventoryMock(this);
	}

	/**
	 * Get the name for this inventory.
	 * Uses the value in {@link #getCustomTitle()} if set, otherwise
	 * uses the default name for the inventory type.
	 *
	 * @return The inventory name.
	 *
	 * @see InventoryMock#getCustomTitle()
	 * @see InventoryType#defaultTitle()
	 */
	@ApiStatus.Internal
	public @NotNull Component getTitle()
	{
		Component custom = getCustomTitle();
		if (custom != null)
		{
			return custom;
		}
		return getType().defaultTitle();
	}

	/**
	 * Get the custom title to be used in this inventory when set.
	 *
	 * @return The title to be used, or {@code null}.
	 */
	@ApiStatus.Internal
	public @Nullable Component getCustomTitle()
	{
		return customTitle;
	}

	/**
	 * Set the custom title to be used in this inventory.
	 *
	 * @param customTitle The title to be used, or {@code null}.
	 */
	@ApiStatus.Internal
	public void setCustomTitle(@Nullable Component customTitle)
	{
		this.customTitle = customTitle;
	}

	/**
	 * Check if two inventories are identical.
	 * <p>
	 * An inventory is considered as identical if the following properties match:
	 * <ul>
	 *     <li>Has the same inventory type.</li>
	 *     <li>Has the same inventory holder.</li>
	 *     <li>Has the same items and quantities.</li>
	 *     <li>Has the same maximum stack size.</li>
	 *     <li>Has the same custom title</li>
	 * </ul>
	 *
	 * @param inventory The other inventory to compare.
	 *
	 * @return {@code true} when identical, otherwise {@code false}
	 */
	@ApiStatus.Internal
	public boolean isIdentical(@Nullable Inventory inventory)
	{
		if (!(inventory instanceof InventoryMock that))
		{
			return false;
		}

		return maxStackSize == that.maxStackSize
				&& Objects.deepEquals(items, that.items)
				&& Objects.equals(holder, that.holder)
				&& type == that.type
				&& Objects.equals(customTitle, that.customTitle);
	}

	/** Note: does not compare holder or viewers (matches spigot/paper). */
	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof InventoryMock that)) return false;
		return maxStackSize == that.maxStackSize
				&& Objects.deepEquals(items, that.items)
				&& type == that.type;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(Arrays.hashCode(items), type, maxStackSize);
	}

	@Override
	public String toString()
	{
		return "InventoryMock{" +
				"type=" + type +
				", maxStackSize=" + maxStackSize +
				", holder=" + (holder != null ? Objects.toIdentityString(holder) : null) +
				", viewers=" + viewers.size() +
				", items=" + Arrays.toString(items) +
				'}';
	}
}
