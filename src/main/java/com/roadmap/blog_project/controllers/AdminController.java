package com.roadmap.blog_project.controllers;

import com.roadmap.blog_project.models.Article;
import com.roadmap.blog_project.services.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController
{
    private final ArticleService articleServ;
    private final String uploadDir = "src/main/resources/static/images/";


    public AdminController(ArticleService articleService)
    {
        this.articleServ = articleService;
    }


    @GetMapping("")
    public String adminView (Model model)
    {
        List<Article> articles = articleServ.getAllArticles();

        if (!articles.isEmpty())
        {
            model.addAttribute("articles", articles);
        }

        model.addAttribute("isAdmin", true);

        return "admin";
    }

    @GetMapping("/article/{title}")
    public String viewArticle (@PathVariable("title") String articleTitle,
                               Model model)
    {
        Article article = articleServ.findByTitle(articleTitle);

        if (article != null)
        {
            model.addAttribute("article", article);
            model.addAttribute("isAdmin", true);
            return "viewArticle";
        }
        else
        {
            return "error/404";
        }
    }

    @GetMapping("/articles")
    public String listArticles (Model model)
    {
        List<Article> listOfArticles = articleServ.getAllArticles();

        if (listOfArticles == null)
        {
            return "error/404";
        }

        model.addAttribute("listOfArticles", listOfArticles);
        model.addAttribute("isAdmin", true);

        return "listArticles";
    }

    @GetMapping("/article/create")
    public String createArticleView (Model model)
    {
        model.addAttribute("article", new Article());

        return "createArticle";
    }

    @PostMapping("/article/create")
    public String createArticle(@ModelAttribute Article article,
                                @RequestParam("articleImage") MultipartFile articleImage,
                                Model model, BindingResult bindingResult)
    {
        if (bindingResult.hasErrors())
        {
            return "createArticle";
        }

        if (!articleImage.isEmpty())
        {
            try
            {
                String fileName = articleImage.getOriginalFilename();
                Path path = Paths.get(uploadDir + fileName);

                Files.write(path, articleImage.getBytes());

                article.setImage("/static/images/" + fileName);
            }
            catch (IOException e)
            {
                model.addAttribute(
                        "errorMessage",
                        "Image upload failed: " + e.getMessage()
                );

                return "createArticle";
            }
        }

        boolean isArticleSaved = articleServ.saveArticle(article);

        if (!isArticleSaved)
        {
            model.addAttribute(
                    "errorMessage",
                    "Article with title '" + article.getTitle() + "' already exists."
                    );

            return "createArticle";
        }

        return "redirect:/admin";
    }

    @GetMapping("/article/{title}/edit")
    public String editArticleView (@PathVariable("title") String articleTitle,Model model)
    {

        Article existingArticle = articleServ.findByTitle(articleTitle);

        if (existingArticle == null)
        {
            return "articleNotFound";
        }

        model.addAttribute("article", existingArticle);
        model.addAttribute("isAdmin", true);

        return "updateArticle";
    }

    @PatchMapping("/article/{title}/edit")
    public String updateArticle(@PathVariable("title") String articleTitle,
                                @ModelAttribute Article updatedArticle,
                                @RequestParam("articleImage") MultipartFile articleImage,
                                BindingResult bindingResult,
                                Model model)
    {
        if (bindingResult.hasErrors())
        {
            return "updateArticle";
        }

        if (!articleImage.isEmpty())
        {
            try
            {
                String fileName = articleImage.getOriginalFilename();
                Path path = Paths.get(uploadDir + fileName);

                Files.write(path, articleImage.getBytes());

                updatedArticle.setImage("/static/images/" + fileName);
            }
            catch (IOException e)
            {
                model.addAttribute(
                        "errorMessage",
                        "Image upload failed: " + e.getMessage()
                );

                return "createArticle";
            }
        }


        boolean isArticleUpdated = articleServ.updateArticle(articleTitle, updatedArticle);

        if (!isArticleUpdated)
        {
            model.addAttribute(
                    "errorMessage",
                    "Article with title " + articleTitle + " couldn't be updated.");

            return "updateArticle";
        }

        return "redirect:/admin";
    }

    @GetMapping("/article/{title}/delete")
    public String deleteArticleView (@PathVariable("title") String articleTitle,Model model)
    {

        Article existingArticle = articleServ.findByTitle(articleTitle);

        if (existingArticle == null)
        {
            return "articleNotFound";
        }

        model.addAttribute("isAdmin", true);

        model.addAttribute("article", existingArticle);

        return "deleteArticle";
    }

    @DeleteMapping("/article/{title}/delete")
    public String deleteArticle(@PathVariable("title") String articleTitle,
                                @ModelAttribute Article updatedArticle,
                                Model model)
    {


        boolean wasArticleRemoved = articleServ.deleteArticle(articleTitle);

        if (!wasArticleRemoved)
        {
            model.addAttribute(
                    "errorMessage",
                    "Article with title " + articleTitle + " couldn't be deleted.");

            return "deleteArticle";
        }

        return "redirect:/admin";
    }

}
