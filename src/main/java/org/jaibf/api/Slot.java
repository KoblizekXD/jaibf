package org.jaibf.api;

/**
 * This annotation is used to bind a field into a schema-defined slot.
 * The field must be a field with a type that is one of the following:
 * <ul>
 *     <li>{@link org.bukkit.Material}</li>
 *     <li>{@link org.bukkit.inventory.ItemStack}</li>
 * </ul>
 */
public @interface Slot {
    /**
     * Corresponds to the identifier of the slot in the schema.
     */
    String value();

    /**
     * Binds the slot to a specific page in the inventory.
     * If the page is switched into an inventory, this slot is not present on,
     * the field will be set to {@code null}.
     */
    String page() default "";
}
