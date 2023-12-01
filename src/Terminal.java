import java.util.Scanner;

public class Terminal {
    private FileSystem fileSystem;
    private User currentUser;

    public Terminal() {
        this.fileSystem = new FileSystem();
        this.currentUser = fileSystem.getUserList().stream()
                .filter(user -> user.getUsername().equals("root"))
                .findFirst()
                .orElseGet(() -> {
                    User rootUser = new User("root", 1, true);
                    fileSystem.adduser(rootUser);
                    return rootUser;
                });
    }

    public void start() {
        System.out.println("Fake UNIX v2.5 @ Gustavo Haag");

        Scanner scanner = new Scanner(System.in);
        String command;

        do {
            System.out.print(currentUser.getUsername() + "@"
                    + fileSystem.getCurrentDirectory().getName() + ":~$ ");
            command = scanner.nextLine();
            executeCommand(command);
        } while (!command.equals("exit"));

        System.out.println("Terminal closed.");
    }

    private void executeCommand(String command) {
        String[] parts = command.split(" ");

        switch (parts[0]) {
            case "formata":
                fileSystem.formata();
                User rootUser = new User("root", 1, true);
                fileSystem.adduser(rootUser);
                break;
            case "touch":
                if (currentUser != null) {
                    fileSystem.touch(parts[1], currentUser);
                } else {
                    System.out.println("Error: User not logged in.");
                }
                break;
            case "gravar_conteudo":
                if (currentUser != null) {
                    fileSystem.gravar_conteudo(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), parts[4]);
                } else {
                    System.out.println("Error: User not logged in.");
                }
                break;
            case "cat":
                if (currentUser != null) {
                    fileSystem.cat(parts[1]);
                } else {
                    System.out.println("Error: User not logged in.");
                }
                break;
            case "rm":
                if (currentUser != null) {
                    fileSystem.rm(parts[1]);
                } else {
                    System.out.println("Error: User not logged in.");
                }
                break;
            case "chown":
                if (currentUser != null) {
                    fileSystem.chown(parts[1], parts[2], parts[3]);
                } else {
                    System.out.println("Error: User not logged in.");
                }
                break;
            case "chmod":
                if (currentUser != null) {
                    fileSystem.chmod(parts[1], parts[2]);
                } else {
                    System.out.println("Error: User not logged in.");
                }
                break;
            case "mkdir":
                if (currentUser != null) {
                    fileSystem.mkdir(parts[1], currentUser);
                } else {
                    System.out.println("Error: User not logged in.");
                }
                break;
            case "rmdir":
                if (currentUser != null) {
                    fileSystem.rmdir(parts[1], currentUser);
                } else {
                    System.out.println("Error: User not logged in.");
                }
                break;
            case "cd":
                if (currentUser != null) {
                    fileSystem.cd(parts[1], currentUser);
                } else {
                    System.out.println("Error: User not logged in.");
                }
                break;
            case "ls":
                if (currentUser != null) {
                    fileSystem.ls();
                } else {
                    System.out.println("Error: User not logged in.");
                }
                break;
            case "adduser":
                if (currentUser != null) {
                    fileSystem.adduser(parts[1], parts[2]);
                } else {
                    System.out.println("Error: User not logged in.");
                }
                break;
            case "rmuser":
                if (currentUser != null) {
                    fileSystem.rmuser(parts[1]);
                } else {
                    System.out.println("Error: User not logged in.");
                }
                break;
            case "lsuser":
                if (currentUser != null) {
                    fileSystem.lsuser();
                } else {
                    System.out.println("Error: User not logged in.");
                }
                break;
            case "exit":
                break;
            default:
                System.out.println("Error: Unknown command.");
        }
    }

    public static void main(String[] args) {
        Terminal terminal = new Terminal();
        terminal.start();
    }
}