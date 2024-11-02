package com.roadmap.blog_project.services;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadmap.blog_project.models.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ArticleService
{
    private static final Logger log = LoggerFactory.getLogger(ArticleService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final String articlesFilePath = "src/main/resources/articles/articles.json";
    private final String uploadDir = "src/main/resources/static/images/";
    private final Lock fileLock = new ReentrantLock();

    public boolean saveArticle(Article newArticle)
    {
        fileLock.lock();

        try
        {
            List<Article> articles = loadArticles();

            if (articles.stream().noneMatch(
                    existingArticle -> existingArticle.getTitle().equals(newArticle.getTitle())
            ))
            {
                newArticle.setCreatedAt(new Date());

                articles.add(newArticle);
                writeArticlesToFile(articles);

                return true;
            }
            else
            {
                log.info("Article with title '{}' already exists. Not saving the article.", newArticle.getTitle());

                return false;
            }
        }
        catch (IOException e)
        {
            log.info("There was an issue saving article: {}", e.getMessage());

            return false;
        }
        finally
        {
            fileLock.unlock();
        }
    }

    private List<Article> loadArticles() throws IOException
    {
        File articlesFile = new File(articlesFilePath);

        if (!articlesFile.exists())
        {
            if(articlesFile.createNewFile())
            {
                writeArticlesToFile(new ArrayList<>());

                return new ArrayList<>();
            }
            else
            {
                throw new IOException("Could not create articles file");
            }
        }

        if (articlesFile.length() == 0)
        {
            return new ArrayList<>();
        }

        return objectMapper.readValue(
                articlesFile, new TypeReference<List<Article>>() {}
        );
    }

    private void writeArticlesToFile(List<Article> articles) throws IOException
    {
        objectMapper.writeValue(new File(articlesFilePath), articles);
    }


    public List<Article> getAllArticles()
    {
        try
        {
            return loadArticles();
        }
        catch (IOException e)
        {
            log.error("There was an issue loading articles: {}", e.getMessage(), e);

            return new ArrayList<>();
        }
    }

    public Article findByTitle(String articleTitle)
    {
        try
        {
            List<Article> articles = loadArticles();

            return articles.stream().filter(
                    article -> article.getTitle().equals(articleTitle)
            ).findFirst().orElse(null);
        }
        catch (IOException e)
        {
            log.error("There was an issue finding the article: {}", e.getMessage(), e);

            return null;
        }
    }

    public boolean updateArticle(String articleTitle, Article updatedArticle)
    {
        fileLock.lock();

        try
        {
            List<Article> articles = loadArticles();

            for (int i = 0; i < articles.size(); i++)
            {
                if (articles.get(i).getTitle().equals(articleTitle))
                {
                    updatedArticle.setCreatedAt(articles.get(i).getCreatedAt());

                    articles.set(i, updatedArticle);

                    writeArticlesToFile(articles);

                    return true;
                }
            }

            return false;
        }
        catch (IOException e)
        {
            log.error("There was an issue updating the article: {}", e.getMessage(), e);

            return false;
        }
        finally
        {
            fileLock.unlock();
        }

    }

    public boolean deleteArticle (String articleTitle)
    {
        fileLock.lock();

        try
        {
            List<Article> articles = loadArticles();

            Article articleToDelete = findByTitle(articleTitle);

            boolean wasArticleRemoved = articles.removeIf(
                    article -> article.getTitle().equals(articleTitle)
            );

            if (wasArticleRemoved)
            {
                writeArticlesToFile(articles);
                deleteArticleImage(articleToDelete.getImage());
            }

            return wasArticleRemoved;

        }
        catch (IOException e)
        {
            log.error("There was an issue deleting the article: {}", e.getMessage(), e);

            return false;
        }
        finally {
            fileLock.unlock();
        }
    }

    private void deleteArticleImage(String image)
    {
        if (image != null && !image.isEmpty())
        {
            try
            {
                Path path = Paths.get("src/main/resources" + image);

                Files.deleteIfExists(path);
                log.info("Deleted image file: {}", path);
            }
            catch (IOException e)
            {
                log.error("There was an issue deleting the image file: {}", e.getMessage(), e);
            }
        }
    }


}
