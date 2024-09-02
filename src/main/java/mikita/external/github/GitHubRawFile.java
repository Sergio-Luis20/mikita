package mikita.external.github;

import lombok.Getter;
import mikita.external.RawFile;

import java.net.MalformedURLException;
import java.net.URI;

@Getter
public class GitHubRawFile extends RawFile {

    public static final String DOWNLOAD_URL_PREFIX = "https://raw.githubusercontent.com";

    private String repositoryOwner;
    private String repository;
    private String branch;
    private String path;

    public GitHubRawFile(String repositoryOwner, String repository, String path) throws MalformedURLException {
        this(repositoryOwner, repository, "master", path);
    }

    public GitHubRawFile(String repositoryOwner, String repository, String branch, String path) throws MalformedURLException {
        super(URI.create(String.join("/", DOWNLOAD_URL_PREFIX, repositoryOwner, repository, branch, path.startsWith("/") ? path.substring(1) : path)).toURL());
    }

}
