LEETCODE_GRAPHQL_URL = "https://leetcode.com/graphql/"
HEADERS = {
    "Content-Type": "application/json",
    "User-Agent": "Mozilla/5.0",
    "Origin": "https://leetcode.com"
}
SOLUTIONS_DIR = "../../solutions"

communitySolution_query = """
query communitySolution($topicId: Int!) {
  topic(id: $topicId) {
    post {
      content
    }
  }
}
"""

communitySolutions_query = """
query communitySolutions($questionSlug: String!, $skip: Int!, $first: Int!, $query: String, $orderBy: TopicSortingOption, $languageTags: [String!], $topicTags: [String!]) {
  questionSolutions(
    filters: {questionSlug: $questionSlug, skip: $skip, first: $first, query: $query, orderBy: $orderBy, languageTags: $languageTags, topicTags: $topicTags}
  ) {
    solutions {
      id
    }
  }
}
"""
