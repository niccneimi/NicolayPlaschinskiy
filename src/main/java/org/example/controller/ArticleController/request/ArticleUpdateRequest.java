package org.example.controller.ArticleController.request;

import java.util.Set;
import org.example.id.ArticleId;

public record ArticleUpdateRequest(ArticleId articleID, String name, Set<String> tags) {}