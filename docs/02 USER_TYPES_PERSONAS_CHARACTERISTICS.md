# User Types, Personas, and Characteristics

## 2. Overview
This document defines the various user types, detailed personas, and access characteristics for the University Institutional Academic Repository (UIAR) system. Understanding these user profiles is essential for designing appropriate user experiences, security models, and feature sets that meet the diverse needs of our stakeholders.

### Related Documentation
- **System Scope**: See [System Scope](./01%20SYSTEM_SCOPE.md) for scope boundaries that inform user requirements
- **Functional Features**: See [Functional Requirements](./03%20FUNCTIONAL_REQUIREMENTS.md) for features aligned with user needs
- **Security Implementation**: See [Non-Functional Requirements](./05%20NON_FUNCTIONAL_REQUIREMENTS.md#51-security-requirements) for security measures protecting user data
- **Development Priority**: See [Phased Development Roadmap](./07%20PHASED_DEVELOPMENT_ROADMAP.md) for user feature implementation timeline

## 2.1. User Role Definitions

### 2.1.1. Public User / Guest
**Description**: Any individual accessing the website without logging in. This represents the broadest user category and includes prospective students, researchers from other institutions, industry recruiters, journalists, and the general public.

**Primary Use Cases**:
- Discovering faculty research and expertise
- Accessing published academic content
- Evaluating university research capabilities
- Finding potential collaboration opportunities
- Academic reference and citation purposes

**Access Level**: Read-only access to published content with no content modification privileges.

### 2.1.2. Faculty Member / Authenticated Contributor
**Description**: A verified university faculty member or researcher with login credentials. They are the primary content creators for their own work and the core users of the content management features.

**Primary Use Cases**:
- Managing personal academic profiles
- Publishing and updating research works
- Controlling visibility and access to their content
- Showcasing expertise for collaboration opportunities
- Tracking engagement and impact metrics

**Access Level**: Full control over their own content with ability to create, edit, publish, and manage their academic works and profile information.

### 2.1.3. Administrator / Authenticated Superuser
**Description**: Designated staff (e.g., from a department, library, or IT) with elevated privileges. This role may be subdivided into DepartmentAdmin and SuperAdmin in later phases to provide more granular control.

**Primary Use Cases**:
- Managing faculty profiles and content
- Overseeing system configuration and settings
- Moderating content and ensuring quality standards
- Managing featured content and department showcases
- Providing technical support to faculty users

**Access Level**: Administrative privileges including user management, content moderation, and system configuration capabilities.

## 2.2. User Persona Narratives

### 2.2.1. Persona 1: Dr. Arisara Sukhsawat (Faculty Member)
**Role**: Associate Professor in Materials Science  
**Career Stage**: Mid-career (8-12 years post-PhD)  
**Technical Proficiency**: Moderate  

**Background and Goals**:
Dr. Sukhsawat is an established researcher who wants an easy way to maintain an up-to-date list of her publications without the burden of managing a separate personal website. She aims to attract talented graduate students and is open to industry collaborations that align with her research in advanced materials and nanotechnology.

**Pain Points**:
- **Time Management**: The time it takes to update profiles across multiple platforms (university website, Google Scholar, ResearchGate, LinkedIn)
- **Technical Barriers**: Limited web development skills make maintaining a personal website challenging
- **Visibility Concerns**: Difficulty in ensuring her research reaches the right audience
- **Student Recruitment**: Challenges in attracting high-quality graduate students to her research group

**User Journey**:
1. Logs into the system monthly to update her profile
2. Uploads new publications as they are accepted/published
3. Reviews and responds to collaboration inquiries
4. Monitors profile views and download statistics
5. Updates research interests and project descriptions

**Success Criteria**:
- Can update her complete profile in under 15 minutes
- Receives measurably more inquiries from prospective students
- Sees increased downloads and citations of her work
- Spends less time on administrative profile maintenance

### 2.2.2. Persona 2: Nattapong Charoen (Prospective Student)
**Role**: Final-year undergraduate student  
**Institution**: External university  
**Research Interests**: AI and robotics  
**Technical Proficiency**: High  

**Background and Goals**:
Nattapong is looking for a Ph.D. advisor and needs to efficiently identify faculty whose research aligns with his interests in artificial intelligence and robotics. He requires easy access to recent publications, research descriptions, and contact information to make informed decisions about potential advisors.

**Pain Points**:
- **Information Fragmentation**: Faculty information scattered across multiple platforms
- **Outdated Information**: University websites often contain stale or incomplete faculty profiles
- **Research Depth**: Difficulty assessing the depth and direction of faculty research
- **Contact Barriers**: Challenges in finding appropriate contact methods and timing

**User Journey**:
1. Searches for faculty by research keywords (AI, robotics, machine learning)
2. Filters results by department, recent publications, and research activity
3. Reviews faculty profiles and recent publications
4. Downloads relevant papers to assess research fit
5. Compiles contact information for outreach
6. Tracks application deadlines and requirements

**Success Criteria**:
- Can identify 5-10 potential advisors within 2 hours of searching
- Finds comprehensive, up-to-date information for each faculty member
- Successfully contacts faculty with relevant research alignment
- Makes informed decisions about graduate program applications

### 2.2.3. Persona 3: Ms. Siriporn Kittichai (Department Administrator)
**Role**: Head of Administration, Computer Science Department  
**Technical Proficiency**: Moderate to High  
**Experience**: 5+ years in academic administration  

**Background and Goals**:
Ms. Kittichai is responsible for ensuring the department's public information accurately represents its strengths and capabilities. She needs to maintain faculty profiles, assist less tech-savvy professors with platform usage, and potentially manage featured research sections for the department's homepage.

**Pain Points**:
- **Faculty Compliance**: Some faculty members are reluctant to maintain their profiles
- **Content Quality**: Ensuring consistent quality and completeness across all faculty profiles
- **Technical Support**: Providing assistance to faculty with varying technical skills
- **Department Representation**: Showcasing department strengths effectively to external audiences

**User Journey**:
1. Reviews department faculty profiles for completeness and accuracy
2. Assists faculty with profile updates and publication uploads
3. Manages featured research and department highlights
4. Monitors system usage and identifies faculty needing support
5. Coordinates with IT for technical issues and system updates
6. Generates reports on department research activity and visibility

**Success Criteria**:
- Maintains 95%+ profile completeness across department faculty
- Reduces faculty support requests through improved user experience
- Successfully showcases department research strengths
- Achieves measurable increases in department visibility and inquiries

## 2.3. User Roles and Access Rights Table

**Critical Importance**: This table directly informs the implementation of security rules (e.g., using Spring Security annotations). Defining access control upfront designs security into the system from the start, rather than adding it as an afterthought. This reduces ambiguity about access control, addressing the OWASP #1 risk: "Broken Access Control".

| Feature / Action | Public User | Faculty Member | Administrator |
|------------------|-------------|----------------|---------------|
| **Content Viewing** | | | |
| View Published Works | ✅ Allow | ✅ Allow | ✅ Allow |
| View Faculty Profiles | ✅ Allow | ✅ Allow | ✅ Allow |
| Search Works | ✅ Allow | ✅ Allow | ✅ Allow |
| Advanced Search Filters | ✅ Allow | ✅ Allow | ✅ Allow |
| **Content Access** | | | |
| Download PDF Files | ✅ Allow | ✅ Allow | ✅ Allow |
| View Contact Information | ✅ Allow | ✅ Allow | ✅ Allow |
| Export Citations | ✅ Allow | ✅ Allow | ✅ Allow |
| **Content Creation** | | | |
| Create User Account | ❌ Deny | ❌ Deny* | ✅ Allow |
| Create New Work | ❌ Deny | ✅ Allow | ✅ Allow |
| Upload Files | ❌ Deny | ✅ Allow | ✅ Allow |
| Create Draft Works | ❌ Deny | ✅ Allow | ✅ Allow |
| **Content Management (Own)** | | | |
| Edit Own Profile | ❌ Deny | ✅ Allow | ✅ Allow |
| Edit Own Work (Draft) | ❌ Deny | ✅ Allow | ✅ Allow |
| Edit Own Work (Published) | ❌ Deny | ✅ Allow | ✅ Allow |
| Delete Own Work | ❌ Deny | ✅ Allow** | ✅ Allow |
| Change Work Status | ❌ Deny | ✅ Allow | ✅ Allow |
| Manage Work Visibility | ❌ Deny | ✅ Allow | ✅ Allow |
| **Content Management (Others)** | | | |
| View Others' Drafts | ❌ Deny | ❌ Deny | ✅ Allow |
| Edit Others' Profiles | ❌ Deny | ❌ Deny | ✅ Allow |
| Edit Others' Works | ❌ Deny | ❌ Deny | ✅ Allow |
| Delete Others' Works | ❌ Deny | ❌ Deny | ✅ Allow*** |
| **System Administration** | | | |
| Manage All User Profiles | ❌ Deny | ❌ Deny | ✅ Allow |
| Manage Featured Content | ❌ Deny | ❌ Deny | ✅ Allow |
| System Configuration | ❌ Deny | ❌ Deny | ✅ Allow |
| View Analytics Dashboard | ❌ Deny | ❌ Deny | ✅ Allow |
| Generate Reports | ❌ Deny | ❌ Deny | ✅ Allow |
| **User Management** | | | |
| Create Faculty Accounts | ❌ Deny | ❌ Deny | ✅ Allow |
| Disable User Accounts | ❌ Deny | ❌ Deny | ✅ Allow |
| Reset User Passwords | ❌ Deny | ❌ Deny | ✅ Allow |
| Assign User Roles | ❌ Deny | ❌ Deny | ✅ Allow |

### Footnotes:
- **\*** Faculty accounts are created by administrators, not self-registered
- **\*\*** Faculty can delete their own works with confirmation workflow
- **\*\*\*** Administrators require additional confirmation for destructive operations

## 2.4. Access Control Implementation Guidelines

### 2.4.1. Spring Security Annotations
```java
// Example security annotations for implementation reference
@PreAuthorize("hasRole('ADMIN') or @securityService.isOwner(authentication.name, #workId)")
@PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
@PreAuthorize("permitAll()")
```

### 2.4.2. Role Hierarchy
```
ADMIN (highest privilege)
├── FACULTY (content creation and management)
└── PUBLIC (read-only access)
```

### 2.4.3. Content Visibility Levels
- **Public**: Visible to all users (including unauthenticated)
- **Institutional**: Visible to authenticated university users only
- **Private**: Visible to owner and administrators only
- **Draft**: Visible to owner and administrators only

*Note: These visibility levels align with the security requirements detailed in [Non-Functional Requirements §5.1](./05%20NON_FUNCTIONAL_REQUIREMENTS.md#51-security-requirements)*

## 2.5. User Experience Considerations

### 2.5.1. Public User Experience
- **Intuitive Navigation**: Clear search and browsing capabilities
- **Mobile Responsiveness**: Optimized for mobile and tablet access
- **Performance**: Fast loading times for content discovery
- **Accessibility**: WCAG 2.1 AA compliance for inclusive access

### 2.5.2. Faculty User Experience
- **Simplified Workflows**: Minimal steps for common tasks
- **Bulk Operations**: Efficient handling of multiple publications
- **Progress Saving**: Auto-save functionality for long-form content
- **Notification System**: Updates on profile views and collaboration requests

### 2.5.3. Administrator Experience
- **Dashboard Overview**: Comprehensive system status and activity monitoring
- **Batch Operations**: Efficient management of multiple users and content
- **Reporting Tools**: Built-in analytics and export capabilities
- **Support Features**: Tools for assisting faculty users

## 2.6. Security and Privacy Considerations

### 2.6.1. Data Protection
- **Personal Information**: Minimal collection with explicit consent
- **Content Ownership**: Clear attribution and ownership tracking
- **Privacy Controls**: Granular visibility settings for all content
- **Data Retention**: Compliance with institutional data policies

### 2.6.2. Authentication and Authorization
- **Multi-Factor Authentication**: Optional for enhanced security
- **Session Management**: Secure session handling with appropriate timeouts
- **Audit Logging**: Comprehensive logging of all user actions
- **Access Reviews**: Regular review of user permissions and roles

## 2.7. Future User Role Considerations

### 2.7.1. Planned Role Expansions
- **Department Administrator**: Department-specific administrative privileges
- **External Collaborator**: Limited access for external research partners
- **Student Researcher**: Supervised access for graduate student content
- **Alumni Researcher**: Continued access for alumni maintaining research profiles

### 2.7.2. Integration Roles
- **API User**: Programmatic access for external system integration
- **Service Account**: Automated system operations and maintenance
- **Read-Only Analyst**: Access for institutional research and analytics

---

**Document Version**: 1.0  
**Last Updated**: August 12, 2025  
**Next Review**: September 12, 2025  

## Related Documentation

### Core Documentation Suite
1. **[System Scope](./01%20SYSTEM_SCOPE.md)** - System boundaries and objectives that inform user requirements
2. **[Functional Requirements](./03%20FUNCTIONAL_REQUIREMENTS.md)** - Feature specifications aligned with user personas
3. **[Data and Information Architecture](./04%20DATA_INFORMATION_ARCHITECTURE.md)** - Data structures supporting user roles
4. **[Non-Functional Requirements](./05%20NON_FUNCTIONAL_REQUIREMENTS.md)** - Security and performance requirements for user protection
5. **[System Architecture and Technology Stack](./06%20SYSTEM_ARCHITECTURE_TECHNOLOGY_STACK.md)** - Technical implementation of user management
6. **[Phased Development Roadmap](./07%20PHASED_DEVELOPMENT_ROADMAP.md)** - User feature implementation timeline

### Key Cross-References
- **Access Control Implementation**: User roles inform RBAC design in Documents 05 and 06
- **Feature Prioritization**: User personas guide feature development in Document 03
- **Security Requirements**: User data protection requirements detailed in Document 05
- **Development Planning**: User role implementation prioritized in Document 07
- **Data Requirements**: User data structures specified in Document 04

**Implementation Priority**: High - Foundation for all security and UX design
