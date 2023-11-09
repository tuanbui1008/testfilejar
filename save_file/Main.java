 
-import org.apache.log4j.BasicConfigurator;
 import org.eclipse.jgit.api.Git;
 import org.eclipse.jgit.api.errors.GitAPIException;
 import org.eclipse.jgit.diff.DiffEntry;
@@ -17,10 +16,10 @@
 
 public class Main {
     public static void main(String[] args) throws IOException, GitAPIException {
-        System.out.println("Hello world!");
-        BasicConfigurator.configure();
         // Đường dẫn đến thư mục repository
-        String repositoryPath = "D:/demo/demo";
        final String dir = System.getProperty("user.dir");
        System.getProperty("user.dir");
        String repositoryPath = System.getProperty("user.dir");
 
         // Tạo đối tượng Git từ đường dẫn repository
         try (Git git = Git.open(new File(repositoryPath))) {
@@ -28,7 +27,7 @@
             ObjectId masterCommitId = git.getRepository().resolve("master");
 
             // Lấy ra ObjectId của commit trên nhánh khác
-            ObjectId otherCommitId = git.getRepository().resolve("test-file");
            ObjectId otherCommitId = git.getRepository().resolve(git.getRepository().getBranch());
 
             // Lấy ra danh sách các tệp thay đổi giữa hai commit
             List<DiffEntry> diffEntries = git.diff()
@@ -42,8 +41,8 @@
                 }
                 String[] path = diffEntry.getNewPath().split("/");
                 String pathFile = path[path.length - 1];
-                File file = new File("D:/save_file/" + pathFile);
-                File fileBefore = new File("D:/save_file/" + "before" + pathFile);
                File file = new File(repositoryPath + "/save_file/" + pathFile);
                File fileBefore = new File(repositoryPath + "/save_file/" + "before" + pathFile);
                 if (!file.getName().endsWith(".java")) {
                     continue;
                 }
@@ -74,7 +73,7 @@
                 reader.close();
                 writer.close();
             }
-            String folderPath = "D:/save_file";
            String folderPath = repositoryPath + "/save_file";
             File folder = new File(folderPath);
 
             if (folder.exists() && folder.isDirectory()) {
