package eu.ibagroup.easyrpa.openframework.googledrive.file;

public class Id {
    private String id;

    public Id(String id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}
