#!/bin/bash

set -e  # Exit on any error

VERSION=""
DRY_RUN=false
FORCE=false

# get parameters
while getopts v:dfh flag
do
  case "${flag}" in
    v) VERSION=${OPTARG};;
    d) DRY_RUN=true;;
    f) FORCE=true;;
    h) echo "Usage: $0 [-v version_type] [-d] [-f] [-h]"
       echo "  -v: Version type (major, minor, patch, auto)"
       echo "  -d: Dry run (don't create tag)"
       echo "  -f: Force tag creation even if commit already tagged"
       echo "  -h: Show this help message"
       echo ""
       echo "Examples:"
       echo "  $0 -v auto              # Auto-detect version bump"
       echo "  $0 -v minor -d          # Dry run for minor bump"
       echo "  $0 -v patch -f          # Force patch bump"
       exit 0;;
    *) echo "Usage: $0 [-v version_type] [-d] [-f] [-h]" >&2
       echo "  -v: Version type (major, minor, patch, auto)" >&2
       echo "  -d: Dry run (don't create tag)" >&2
       echo "  -f: Force tag creation even if commit already tagged" >&2
       echo "  -h: Show help message" >&2
       exit 1;;
  esac
done

# Function to determine version bump from commit messages
determine_version_bump() {
  # Get commits since last tag
  local last_tag=$(git describe --abbrev=0 --tags 2>/dev/null || echo "")
  local commit_range
  
  if [[ -n "$last_tag" ]]; then
    commit_range="${last_tag}..HEAD"
  else
    # If no tags exist, check all commits
    commit_range="HEAD"
  fi
  
  local commits=$(git log --oneline --pretty=format:"%s" $commit_range 2>/dev/null || echo "")
  
  # Check for breaking changes (major)
  if echo "$commits" | grep -qE "^(feat|fix|perf|refactor)(\(.+\))?\!:|^BREAKING CHANGE:|breaking change|major:"; then
    echo "major"
    return
  fi
  
  # Check for new features (minor)
  if echo "$commits" | grep -qE "^feat(\(.+\))?:|^feature:|minor:"; then
    echo "minor"
    return
  fi
  
  # Default to patch for bug fixes and other changes
  if echo "$commits" | grep -qE "^(fix|docs|style|refactor|test|chore)(\(.+\))?:|patch:"; then
    echo "patch"
    return
  fi
  
  # If no conventional commits found, default to patch
  echo "patch"
}

# Fetch latest tags and refs
echo "Fetching latest tags..."
git fetch --prune --unshallow 2>/dev/null || git fetch --tags 2>/dev/null || true

# get highest tag number, and add v0.1.0 if doesn't exist
CURRENT_VERSION=$(git describe --abbrev=0 --tags 2>/dev/null || echo "")

if [[ -z "$CURRENT_VERSION" ]]; then
  CURRENT_VERSION='v0.1.0'
  echo "No existing tags found. Starting with: $CURRENT_VERSION"
else
  echo "Current Version: $CURRENT_VERSION"
fi

# Auto-determine version if not specified
if [[ "$VERSION" == "auto" ]] || [[ -z "$VERSION" ]]; then
  VERSION=$(determine_version_bump)
  echo "Auto-detected version bump: $VERSION"
fi

# Validate version input
if [[ ! "$VERSION" =~ ^(major|minor|patch)$ ]]; then
  echo "❌ Error: Invalid version type '$VERSION'. Must be one of: major, minor, patch, auto" >&2
  exit 1
fi

# Parse current version - handle both v1.2.3 and 1.2.3 formats
if [[ $CURRENT_VERSION =~ ^v?([0-9]+)\.([0-9]+)\.([0-9]+)$ ]]; then
  VNUM1=${BASH_REMATCH[1]}
  VNUM2=${BASH_REMATCH[2]} 
  VNUM3=${BASH_REMATCH[3]}
else
  echo "❌ Error: Invalid version format '$CURRENT_VERSION'. Expected format: v1.2.3 or 1.2.3" >&2
  exit 1
fi

echo "Parsed version: $VNUM1.$VNUM2.$VNUM3"

# Calculate new version based on bump type
if [[ $VERSION == 'major' ]]; then
  VNUM1=$((VNUM1+1))
  VNUM2=0
  VNUM3=0
elif [[ $VERSION == 'minor' ]]; then
  VNUM2=$((VNUM2+1))
  VNUM3=0
elif [[ $VERSION == 'patch' ]]; then
  VNUM3=$((VNUM3+1))
fi

# create new tag
NEW_TAG="v$VNUM1.$VNUM2.$VNUM3"
echo "🏷️  ($VERSION) updating $CURRENT_VERSION to $NEW_TAG"

# get current hash and see if it already has a tag
GIT_COMMIT=$(git rev-parse HEAD)
NEEDS_TAG=$(git describe --contains $GIT_COMMIT 2>/dev/null || echo "")

# Check if this commit already has a tag
if [[ -n "$NEEDS_TAG" ]] && [[ "$FORCE" != true ]]; then
  echo "⚠️  Commit $GIT_COMMIT already has tag: $NEEDS_TAG"
  echo "Use -f flag to force tag creation"
  if [[ -n "$GITHUB_OUTPUT" ]]; then
    echo "git-tag=$CURRENT_VERSION" >> $GITHUB_OUTPUT
  else
    echo "git-tag=$CURRENT_VERSION"
  fi
  exit 0
fi

# Check if tag already exists
if git rev-parse "$NEW_TAG" >/dev/null 2>&1; then
  echo "⚠️  Tag $NEW_TAG already exists"
  if [[ "$FORCE" != true ]]; then
    echo "Use -f flag to force tag creation"
    if [[ -n "$GITHUB_OUTPUT" ]]; then
      echo "git-tag=$NEW_TAG" >> $GITHUB_OUTPUT
    else
      echo "git-tag=$NEW_TAG"
    fi
    exit 0
  else
    echo "🔄 Force flag enabled, will overwrite existing tag"
    git tag -d "$NEW_TAG" 2>/dev/null || true
    git push origin ":refs/tags/$NEW_TAG" 2>/dev/null || true
  fi
fi

if [[ "$DRY_RUN" == true ]]; then
  echo "🔍 DRY RUN: Would create tag $NEW_TAG on commit $GIT_COMMIT"
  if [[ -n "$GITHUB_OUTPUT" ]]; then
    echo "git-tag=$NEW_TAG" >> $GITHUB_OUTPUT
  else
    echo "git-tag=$NEW_TAG"
  fi
  exit 0
fi

# Create and push the tag
echo "📝 Creating tag $NEW_TAG..."
if git tag "$NEW_TAG"; then
  echo "✅ Successfully created tag $NEW_TAG"
  
  echo "📤 Pushing tag to remote..."
  if git push origin "$NEW_TAG"; then
    echo "✅ Successfully pushed tag $NEW_TAG"
  else
    echo "❌ Failed to push tag $NEW_TAG" >&2
    exit 1
  fi
else
  echo "❌ Failed to create tag $NEW_TAG" >&2
  exit 1
fi

# Output for GitHub Actions
if [[ -n "$GITHUB_OUTPUT" ]]; then
  echo "git-tag=$NEW_TAG" >> $GITHUB_OUTPUT
else
  echo "git-tag=$NEW_TAG"
fi

echo "🎉 Successfully tagged release $NEW_TAG"
exit 0
