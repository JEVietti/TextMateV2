package com.textmate.dovaj.textmate.fragments.dummy;

import com.textmate.dovaj.textmate.models.ContactModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ContactContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<ContactModel> ITEMS = new ArrayList<ContactModel>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, ContactModel> ITEM_MAP = new HashMap<String, ContactModel>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createContactItem());
        }
    }

    private static void addItem(ContactModel item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static ContactModel createContactItem() {
        return new ContactModel ("Test");
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

}
