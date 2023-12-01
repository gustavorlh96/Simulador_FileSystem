import java.util.ArrayList;
import java.util.List;

public class Inode {
    private String name;
    private boolean isDirectory;
    private int ownerId;
    private String ownerName;
    private String creationDate;
    private List<Inode> children;
    private Inode parent;
    private boolean readPermission;
    private boolean writePermission;
    private boolean executePermission;
    private int size;
    private String content;

    public Inode(String name, boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.children = new ArrayList<>();
        this.readPermission = true;
        this.writePermission = true;
        this.executePermission = true;
        this.size = 0;
        this.content = "";
    }

    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public List<Inode> getChildren() {
        return children;
    }

    public Inode getParent() {
        return parent;
    }

    public void setParent(Inode parent) {
        this.parent = parent;
    }

    public boolean getReadPermission() {
        return readPermission;
    }

    public void setReadPermission(boolean readPermission) {
        this.readPermission = readPermission;
    }

    public boolean getWritePermission() {
        return writePermission;
    }

    public void setWritePermission(boolean writePermission) {
        this.writePermission = writePermission;
    }

    public boolean getExecutePermission() {
        return executePermission;
    }

    public void setExecutePermission(boolean executePermission) {
        this.executePermission = executePermission;
    }

    public int getSize() {
        return size;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean addChild(Inode child) {
        boolean added = children.add(child);
        if (added) {
            child.setParent(this);
        }
        return added;
    }

    public boolean removeChild(Inode child) {
        return children.remove(child);
    }

    public void updateSize(int newSize) {
        this.size = newSize;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
        this.size = newContent.length();
    }
}