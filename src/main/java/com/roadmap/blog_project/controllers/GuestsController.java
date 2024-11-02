package com.roadmap.blog_project.controllers;


import com.roadmap.blog_project.models.Article;
import com.roadmap.blog_project.services.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.List;

@Controller
public class GuestsController
{
    private final ArticleService articleServ;

    public GuestsController(ArticleService articleServ) {
        this.articleServ = articleServ;
    }

    @GetMapping("/")
    public String home(Model model) throws IOException
    {
        List<Article> articles = articleServ.getAllArticles();

        if (!articles.isEmpty())
        {
            model.addAttribute("articles", articles);

        }

        model.addAttribute("isAdmin", false);

        return "home";
    }

    @GetMapping("/article/{title}")
    public String viewArticle (@PathVariable("title") String articleTitle,
                               Model model)
    {
        Article article = articleServ.findByTitle(articleTitle);

        if (article != null)
        {
            model.addAttribute("article", article);
            model.addAttribute("isAdmin", false);
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
        model.addAttribute("isAdmin", false);

        return "listArticles";
    }

}
