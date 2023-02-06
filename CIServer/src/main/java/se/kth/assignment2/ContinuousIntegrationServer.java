package se.kth.assignment2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.File;
import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
 */
public class ContinuousIntegrationServer extends AbstractHandler
{
    private String repositoryUrl;
    private String branch;

    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        System.out.println(target);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        //Get repository URL and branch from HTTP payload
        repositoryUrl = request.getParameter("repository_url");
        branch = request.getParameter("branch");

        //Clone repository
        try {
            cloneRepository();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        //Checkout branch
        try {
            checkoutBranch();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        
        response.getWriter().println("CI job done");
    }

    private void cloneRepository() throws GitAPIException, IOException {
        //Clone repository
        Git.cloneRepository()
                .setURI(repositoryUrl)
                .setDirectory(new File("Repository"))
                .call();
    }

    private void checkoutBranch() throws GitAPIException, IOException {
        Git git = Git.open(new File("Repository"));
        git.checkout().setName(branch).call();
    }

    // used to start the CI server in command line
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}
