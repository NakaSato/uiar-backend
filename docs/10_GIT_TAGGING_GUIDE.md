# Git Tagging and Release Guide

## Overview

This project uses an automated semantic versioning system that intelligently determines version bumps based on commit messages and provides flexibility for manual control.

## How It Works

### Automatic Version Detection

The system analyzes commit messages since the last tag to determine the appropriate version bump:

- **Major (X.0.0)**: Breaking changes
  - Commit messages with `!` suffix: `feat!: breaking change`
  - Contains `BREAKING CHANGE:` or `breaking change`
  - Prefix with `major:`

- **Minor (x.Y.0)**: New features
  - Commit messages starting with `feat:` or `feature:`
  - Prefix with `minor:`

- **Patch (x.y.Z)**: Bug fixes and other changes
  - Commit messages starting with `fix:`, `docs:`, `style:`, `refactor:`, `test:`, `chore:`
  - Prefix with `patch:`
  - Default fallback for any other changes

### Manual Control

You can override the automatic detection:

```bash
# Force a specific version bump
./init-scripts/git_update.sh -v major
./init-scripts/git_update.sh -v minor
./init-scripts/git_update.sh -v patch

# Let the system auto-detect (recommended)
./init-scripts/git_update.sh -v auto
```

### Skip Releases

Add `[skip-release]` or `[no-release]` to your commit message to skip creating a release:

```bash
git commit -m "docs: update README [skip-release]"
```

## Script Options

The `git_update.sh` script supports several flags:

- `-v <type>`: Version type (`major`, `minor`, `patch`, `auto`)
- `-d`: Dry run (show what would happen without making changes)
- `-f`: Force tag creation (overwrite existing tags)

### Examples

```bash
# Dry run to see what version would be created
./init-scripts/git_update.sh -v auto -d

# Force create a major version even if tag exists
./init-scripts/git_update.sh -v major -f

# Auto-detect version bump
./init-scripts/git_update.sh -v auto
```

## Commit Message Conventions

We follow [Conventional Commits](https://www.conventionalcommits.org/) specification:

### Format
```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

### Types
- `feat`: A new feature (minor bump)
- `fix`: A bug fix (patch bump)
- `docs`: Documentation only changes (patch bump)
- `style`: Changes that do not affect the meaning of the code (patch bump)
- `refactor`: A code change that neither fixes a bug nor adds a feature (patch bump)
- `perf`: A code change that improves performance (patch bump)
- `test`: Adding missing tests or correcting existing tests (patch bump)
- `chore`: Changes to the build process or auxiliary tools (patch bump)

### Breaking Changes
Add `!` after the type or include `BREAKING CHANGE:` in the footer:

```
feat!: remove deprecated API endpoint

BREAKING CHANGE: The /api/v1/old-endpoint has been removed. Use /api/v2/new-endpoint instead.
```

## Examples

### Commit Messages and Their Version Impact

| Commit Message | Version Bump | Reason |
|---|---|---|
| `feat: add user authentication` | Minor | New feature |
| `fix: resolve login bug` | Patch | Bug fix |
| `feat!: redesign API structure` | Major | Breaking change |
| `docs: update API documentation` | Patch | Documentation |
| `BREAKING CHANGE: remove legacy endpoints` | Major | Breaking change |
| `chore: update dependencies [skip-release]` | None | Skipped |

### Workflow Behavior

1. **On PR**: Only runs tests and build
2. **On main branch push**: 
   - Checks if release should be skipped
   - Auto-detects version bump from commits
   - Creates and pushes git tag
   - Builds and pushes Docker images with new tag

## Troubleshooting

### Tag Already Exists
If a tag already exists, the script will warn you and exit. Use `-f` flag to force overwrite.

### No Version Bump Detected
If no conventional commits are found, the system defaults to a patch bump.

### GitHub Actions Failed
Check the workflow logs for specific error messages. Common issues:
- Insufficient permissions to push tags
- Network issues with ECR
- Invalid commit message format

## Best Practices

1. **Use conventional commit messages** for automatic version detection
2. **Use `[skip-release]`** for commits that don't need a release (docs, CI changes)
3. **Test with dry run** before forcing version bumps: `-d` flag
4. **Let the system auto-detect** versions instead of manual specification
5. **Use meaningful commit messages** that clearly describe the change

## Integration with CI/CD

The GitHub Actions workflow automatically:
- ✅ Runs tests and builds on all PRs
- ✅ Creates releases only on main branch pushes
- ✅ Skips releases when requested via commit message
- ✅ Uses semantic versioning based on commit history
- ✅ Pushes Docker images with proper tags to ECR
- ✅ Provides clear logging and error handling

This ensures consistent, predictable releases while maintaining developer productivity.
