package org.example;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.*;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, GitAPIException {
        BasicConfigurator.configure();
        // Đường dẫn đến thư mục repository
        final String dir = System.getProperty("user.dir");
        System.getProperty("user.dir");
        String repositoryPath = System.getProperty("user.dir");

        // Tạo đối tượng Git từ đường dẫn repository
        try (Git git = Git.open(new File(repositoryPath))) {
            // Lấy ra ObjectId của commit trên nhánh master
            ObjectId masterCommitId = git.getRepository().resolve("master");

            // Lấy ra ObjectId của commit trên nhánh khác
            ObjectId otherCommitId = git.getRepository().resolve(git.getRepository().getBranch());

            // Lấy ra danh sách các tệp thay đổi giữa hai commit
            List<DiffEntry> diffEntries = git.diff()
                    .setOldTree(prepareTreeParser(git.getRepository(), masterCommitId))
                    .setNewTree(prepareTreeParser(git.getRepository(), otherCommitId))
                    .call();
            // Sao chép các tệp thay đổi vào một thư mục
            for (DiffEntry diffEntry : diffEntries) {
                if (diffEntry.getNewPath() == "/dev/null") {
                    continue;
                }
                String[] path = diffEntry.getNewPath().split("/");
                String pathFile = path[path.length - 1];
                File file = new File(repositoryPath + "/save_file/" + pathFile);
                File fileBefore = new File(repositoryPath + "/save_file/" + "before" + pathFile);
                if (!file.getName().endsWith(".java")) {
                    continue;
                }
                file.getParentFile().mkdirs();
                try (FileOutputStream fos = new FileOutputStream(fileBefore)) {
                    DiffFormatter diffFormatter = new DiffFormatter(fos);
                    diffFormatter.setRepository(git.getRepository());
                    diffFormatter.format(diffEntry);
                }

                BufferedReader reader = new BufferedReader(new FileReader(fileBefore));
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));

                String line;
                int lineNumber = 0;
                StringBuilder content = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("+")) {
                        line = line.substring(1);
                    }
                    lineNumber++;
                    if (lineNumber <= 6) {
                        continue;
                    }
                    writer.write(line);
                    writer.newLine();
                }
                reader.close();
                writer.close();
            }
            String folderPath = repositoryPath + "/save_file";
            File folder = new File(folderPath);

            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();

                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().contains("before")) {
                            file.delete();
                        }
                    }
                }
            }
        }
    }

    private static CanonicalTreeParser prepareTreeParser(Repository repository, ObjectId objectId) throws IOException {
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(objectId);
            ObjectId treeId = commit.getTree().getId();
            try (ObjectReader reader = repository.newObjectReader()) {
                return new CanonicalTreeParser(null, reader, treeId);
            }
        }

    }
}