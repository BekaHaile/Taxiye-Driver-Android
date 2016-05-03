package product.clicklabs.jugnoo.driver.utils;

import java.util.HashSet;

/**
 * Created by socomo on 11/18/15.
 */
public class ContactsEntityBean {
    private HashSet<String> emails;
    private HashSet<String> phones;
    private HashSet<String> addresses;
    private HashSet<String> name;
    private String contactId;
    private boolean checked = false;

    public ContactsEntityBean() {
        this.emails = new HashSet<String>();
        this.phones = new HashSet<String>();
        this.addresses = new HashSet<String>();
        this.name = new HashSet<String>();
    }


    public HashSet<String> getPhones() {
        return phones;
    }

    public void setPhones(String phone) {
        if (phone == null)
            return;
        this.phones.add(phone.trim());
    }

    public HashSet<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(String address) {
        if (address == null)
            return;
        this.addresses.add(address.trim());
    }

    public void setEmails(String email) {
        if (email == null)
            return;
        this.emails.add(email.trim());
    }

    public HashSet<String> getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null)
            return;
        this.name.add(name.trim());
    }

    public HashSet<String> getEmails() {
        return emails;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
