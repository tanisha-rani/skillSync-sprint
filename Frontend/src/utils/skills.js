const CATEGORY_RULES = [
  { category: 'Backend', keywords: ['java', 'spring', 'node', 'express', 'api', 'mysql', 'sql', 'hibernate', 'jpa', 'mongodb'] },
  { category: 'Frontend', keywords: ['react', 'angular', 'vue', 'javascript', 'typescript', 'html', 'css', 'tailwind', 'next'] },
  { category: 'Data / AI', keywords: ['python', 'data', 'machine learning', 'deep learning', 'ai', 'analytics', 'pandas', 'numpy'] },
  { category: 'DevOps', keywords: ['docker', 'kubernetes', 'aws', 'azure', 'gcp', 'devops', 'terraform', 'jenkins', 'ci/cd'] },
  { category: 'Mobile', keywords: ['android', 'ios', 'flutter', 'react native', 'swift', 'kotlin'] },
];

export function normalizeSkillName(name = '') {
  return name
    .trim()
    .split(/\s+/)
    .map((part) => {
      if (part.length <= 3 && part === part.toUpperCase()) {
        return part;
      }

      return part.charAt(0).toUpperCase() + part.slice(1).toLowerCase();
    })
    .join(' ');
}

export function getSkillKey(name = '') {
  return normalizeSkillName(name).toLowerCase();
}

export function suggestSkillCategory(name = '') {
  const normalizedName = name.trim().toLowerCase();

  if (!normalizedName) {
    return 'Mentorship';
  }

  const matchingRule = CATEGORY_RULES.find((rule) =>
    rule.keywords.some((keyword) => normalizedName.includes(keyword))
  );

  return matchingRule?.category || 'Mentorship';
}
