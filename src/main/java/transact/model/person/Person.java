package transact.model.person;

import static transact.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import transact.commons.util.ToStringBuilder;
import transact.model.entry.Entry;
import transact.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated,
 * immutable.
 */
public class Person implements Entry {

    public static final Person NullPerson = new Person() {

        @Override
        public Name getName() {
            throw new AssertionError("Method should not be called");
        }

        @Override
        public Address getAddress() {
            throw new AssertionError("Method should not be called");
        }

        @Override
        public Phone getPhone() {
            throw new AssertionError("Method should not be called");
        }

        @Override
        public Email getEmail() {
            throw new AssertionError("Method should not be called");
        }

        @Override
        public Set<Tag> getTags() {
            throw new AssertionError("Method should not be called");
        }

        @Override
        public boolean equals(Object other) {
            return other == this;
        }

        @Override
        public String toString() {
            return "";
        }
    };

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * For empty or null person.
     */
    private Person() {
        name = null;
        phone = null;
        email = null;
        address = null;
    }

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, address, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns an immutable tag set, which throws
     * {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSameEntry(Entry otherEntry) {
        if (otherEntry == this) {
            return true;
        }

        if (!(otherEntry instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) otherEntry;
        return otherPerson.getName().equals(getName());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .toString();
    }
}
