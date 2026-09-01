# Database Design for TLF Admin Dashboard

## Tables:
1. Users (user_id, full_name, email, password, role)
2. Volunteers (volunteer_id, user_id(FK), skills, availability, phone)
3. Donations (donation_id, user_id(FK), amount, status)
4. News_Events (article_id, title, content)

## Relationships:
- USERS -> VOLUNTEERS (One-to-One)
- USERS -> DONATIONS (One-to-Many)
