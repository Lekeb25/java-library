import java.util.Objects;

public class Reader {

    private String firstName;
    private String lastName;
    private int id;

    public Reader( String firstName, String lastName, int id ) {
        this.firstName= firstName;
        this.lastName= lastName;
        this.id= id;
    }

    public void showReaderInfos() {
        System.out.println("ID  :  " + id);
        System.out.println("First Name  :  " + firstName);
        System.out.println("Last Name  :  " + lastName);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        Reader reader= (Reader) o;
        return id == reader.id ;
        /*&& because users are given the option to change their name, and if they do so while keeping the same ID,
        they remain the same user (only the ID serves as the unique identifier).
            Objects.equals(firstName, reader.firstName) &&
            Objects.equals(lastName, reader.lastName);
        */
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}