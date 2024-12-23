package org.example.controller.ArticleController.response;

import java.util.List;

public record ArticlesCreateResponse(List<Long> articlesId) {
}