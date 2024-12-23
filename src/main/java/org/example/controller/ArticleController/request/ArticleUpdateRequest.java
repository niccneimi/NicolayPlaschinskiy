package org.example.controller.ArticleController.request;

import java.util.Set;
import org.example.id.ArticleId;

public record ArticleUpdateRequest(ArticleId articlesId, String name, Set<String> tags) {}