import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FileSystem {
    private List<User> userList;
    private List<Inode> inodeList;
    private Inode currentDirectory;
    private User currentUser;

    public FileSystem() {
        this.userList = new ArrayList<>();
        this.inodeList = new ArrayList<>();
        this.currentDirectory = new Inode("/", true);
    }

    public void formata() {
        userList.clear();
        inodeList.clear();
        currentDirectory = new Inode("/", true);
        currentUser = null;
        System.out.println("Disk formatted successfully.");
    }

    public void touch(String name, User user) {
        Inode newFile = new Inode(name, false);
        newFile.setOwnerId(user.getId());
        newFile.setOwnerName(user.getUsername());
        newFile.setCreationDate(getCurrentDate());
        inodeList.add(newFile);
        currentDirectory.addChild(newFile);
        System.out.println("File '" + name + "' created successfully.");
    }

    public void gravar_conteudo(String name, int position, int nbytes, String buffer) {
        Inode file = findInodeByName(name);
        if (file != null && !file.isDirectory()) {
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(name, "rw")) {
                if (position >= 0 && position <= file.getSize()) {
                    randomAccessFile.seek(position);
                    file.setContent(buffer);
                    randomAccessFile.write(buffer.getBytes());
                    System.out.println("Content written to file '" + name + "' at position " + position + ".");
                } else {
                    System.out.println("Error: Invalid position for file '" + name + "'.");
                }
            } catch (IOException e) {
                System.out.println("Error writing to file '" + name + "': " + e.getMessage());
            }
        } else {
            System.out.println("Error: '" + name + "' is not a valid file.");
        }
    }

    public void cat(String name) {
        Inode file = findInodeByName(name);
        if (file != null && !file.isDirectory()) {
            System.out.println("Content of file '" + name + "':");
            System.out.println(file.getContent());
        } else {
            System.out.println("Error: '" + name + "' is not a valid file.");
        }
    }

    public void rm(String name) {
        Inode toRemove = findInodeByName(name);
        if (toRemove != null && !toRemove.isDirectory()) {
            if (currentDirectory.removeChild(toRemove)) {
                inodeList.remove(toRemove);
                System.out.println("'" + name + "' removed successfully.");
            } else {
                System.out.println("Error: Cannot remove '" + name + "'.");
            }
        } else {
            System.out.println("Error: '" + name + "' not found or is a directory." +
                               " To remove a directory please use command rmdir.");
        }
    }

    public void chown(String user1, String user2, String fileName) {
        currentUser = new User("root", 1, true);
        Inode file = findInodeByName(fileName);
        if (file != null && !file.isDirectory()) {
            if (file.getOwnerId() == currentUser.getId()) {
                Optional<User> newOwner = userList.stream().filter(u -> u.getUsername().equals(user1)).findFirst();
                if (newOwner.isPresent()) {
                    file.setOwnerId(newOwner.get().getId());
                    file.setOwnerName(newOwner.get().getUsername());
                    System.out.println("Ownership of file '" + user2 + "' changed to user '" + user1 + "'.");
                } else {
                    System.out.println("Error: User '" + user1 + "' not found.");
                }
            } else {
                System.out.println("Error: You do not have permission to change ownership of '" + user2 + "'.");
            }
        } else {
            System.out.println("Error: '" + user2 + "' is not a valid file.");
        }
    }

    public void chmod(String fileName, String flags) {
        Inode file = findInodeByName(fileName);
        currentUser = new User("root", 1, true);
        if (file != null) {
            if (currentUser != null && file.getOwnerId() == currentUser.getId()) {
                Set<Character> validFlags = new HashSet<>(Arrays.asList('r', 'w', 'x'));

                for (char flag : flags.toCharArray()) {
                    if (!validFlags.contains(flag)) {
                        System.out.println("Error: Invalid flag '" + flag + "'. Use 'r', 'w', or 'x'.");
                        return;
                    }
                }

                file.setReadPermission(flags.contains("r"));
                file.setWritePermission(flags.contains("w"));
                file.setExecutePermission(flags.contains("x"));

                System.out.println("Permissions for file '" + fileName + "' updated successfully.");
            } else {
                System.out.println("Error: You do not have permission to change permissions of '" + fileName + "'.");
            }
        } else {
            System.out.println("Error: '" + fileName + "' is not a valid file.");
        }
    }

    public void mkdir(String directoryName, User currentUser) {
        if (directoryName.equals("..") || directoryName.equals(".")) {
            System.out.println("Error: Invalid directory name.");
            return;
        }

        Inode newDirectory = new Inode(directoryName, true);
        newDirectory.setOwnerId(currentUser.getId());
        newDirectory.setOwnerName(currentUser.getUsername());
        newDirectory.setCreationDate(getCurrentDate());

        if (currentDirectory.addChild(newDirectory)) {
            inodeList.add(newDirectory);
            System.out.println("Directory '" + directoryName + "' created successfully.");
        } else {
            System.out.println("Error: Directory '" + directoryName + "' already exists.");
        }
    }

    public void rmdir(String directoryName, User currentUser) {
        if (directoryName.equals("..") || directoryName.equals(".")) {
            System.out.println("Error: Invalid directory name.");
            return;
        }

        Inode directoryToRemove = findInodeByName(directoryName);

        if (directoryToRemove != null && directoryToRemove.isDirectory()) {
            if (currentDirectory.removeChild(directoryToRemove)) {
                inodeList.remove(directoryToRemove);
                System.out.println("Directory '" + directoryName + "' removed successfully.");
            } else {
                System.out.println("Error: Directory '" + directoryName + "' is not empty.");
            }
        } else {
            System.out.println("Error: '" + directoryName + "' is not a valid directory.");
        }
    }

    public void cd(String directoryName, User currentUser) {
        if (directoryName.equals("..")) {
            if (currentDirectory.getParent() != null) {
                currentDirectory = currentDirectory.getParent();
                System.out.println("Moved to parent directory.");
            } else {
                System.out.println("Error: Already at the root directory.");
            }
        } else if (directoryName.equals(".")) {
            System.out.println("Remained in the current directory.");
        } else {
            Inode newDirectory = findInodeByName(directoryName);
            if (newDirectory != null && newDirectory.isDirectory()) {
                currentDirectory = newDirectory;
                System.out.println("Moved to directory '" + directoryName + "'.");
            } else {
                System.out.println("Error: '" + directoryName + "' is not a valid directory.");
            }
        }
    }

    public void ls() {
        System.out.println("Listing contents of current directory:");

        for (Inode child : currentDirectory.getChildren()) {
            String type = child.isDirectory() ? "d" : "-";
            String owner = child.getOwnerName();
            String permissions = (child.getReadPermission() ? "r" : "-") +
                    (child.getWritePermission() ? "w" : "-") +
                    (child.getExecutePermission() ? "x" : "-");

            System.out.println(type + permissions + " " + owner + " " + child.getSize() + " " + child.getName()
                             + " " + child.getCreationDate());
        }
    }

    public void adduser(String username, String userId) {
        currentUser = new User("root", 1, true);
        if (currentUser != null && currentUser.isAdmin()) {
            Optional<User> existingUser = userList.stream().filter(u -> u.getUsername().equals(username)).findFirst();

            if (existingUser.isPresent()) {
                System.out.println("Error: User '" + username + "' already exists.");
            } else {
                int parsedUserId;
                try {
                    parsedUserId = Integer.parseInt(userId);
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid user ID format.");
                    return;
                }

                User newUser = new User(username, parsedUserId, false);
                userList.add(newUser);
                System.out.println("User '" + username + "' added successfully.");
            }
        } else {
            System.out.println("Error: Permission denied. Only administrator can add users.");
        }
    }

    public void rmuser(String username) {
        if (currentUser.isAdmin()) {
            Optional<User> userToRemove = userList.stream().filter(u -> u.getUsername().equals(username)).findFirst();

            if (userToRemove.isPresent()) {
                userList.remove(userToRemove.get());

                List<Inode> filesToRemove = inodeList.stream()
                        .filter(inode -> inode.getOwnerId() == userToRemove.get().getId())
                        .collect(Collectors.toList());

                for (Inode file : filesToRemove) {
                    currentDirectory.removeChild(file);
                    inodeList.remove(file);
                }

                System.out.println("User '" + username + "' removed successfully.");
            } else {
                System.out.println("Error: User '" + username + "' not found.");
            }
        } else {
            System.out.println("Error: Permission denied. Only administrator can remove users.");
        }
    }

    public void lsuser() {
        currentUser = new User("root", 1, true);    // Esse trecho serve apenas como teste
                                                                        // forçando o usuário atual sempre
        setCurrentUser(currentUser);                                    // ser um admin (root)

        if (currentUser.isAdmin()) {
            System.out.println("Listing all users:");
            for (User user : userList) {
                System.out.println(user.getUsername() + "        " + user.getId() + "        " + user.getRole());
            }
        } else {
            System.out.println("Error: Permission denied. Only administrator can list users.");
        }
    }

    private Inode findInodeByName(String name) {
        for (Inode inode : currentDirectory.getChildren()) {
            if (inode.getName().equals(name)) {
                return inode;
            }
        }
        return null;
    }

    private String getCurrentDate() {
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return dateTimeFormatter.format(currentDate);
    }

    public Inode getCurrentDirectory() {
        return currentDirectory;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void adduser(User rootUser) {
        userList.add(rootUser);
    }
}