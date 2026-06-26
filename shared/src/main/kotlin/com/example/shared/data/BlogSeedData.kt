package com.example.shared.data

import com.example.shared.models.Blog
import com.example.shared.models.Category
import com.example.shared.models.State

object BlogSeedData {
    fun categories(): List<Category> = listOf(
        Category(id = 1, name = "Festivals", iconName = "celebration"),
        Category(id = 2, name = "Hidden Gems", iconName = "explore"),
        Category(id = 3, name = "Culture & Heritage", iconName = "museum"),
        Category(id = 4, name = "Food & Cuisine", iconName = "restaurant"),
        Category(id = 5, name = "Adventure", iconName = "terrain"),
        Category(id = 6, name = "Wildlife", iconName = "pets"),
        Category(id = 7, name = "Beaches", iconName = "beach_access"),
        Category(id = 8, name = "Mountains", iconName = "filter_hdr"),
        Category(id = 9, name = "Spiritual Places", iconName = "spa"),
        Category(id = 10, name = "Historical Monuments", iconName = "account_balance"),
        Category(id = 11, name = "Road Trips", iconName = "directions_car"),
        Category(id = 12, name = "Shopping", iconName = "shopping_bag"),
        Category(id = 13, name = "Local Experiences", iconName = "groups")
    )

    fun states(): List<State> = listOf(
        State(
            id = 1,
            name = "Rajasthan",
            description = "The Land of Kings, majestic forts, and golden deserts.",
            imageUrl = "https://images.unsplash.com/photo-1599661046289-e31897846e41?auto=format&fit=crop&w=800&q=80"
        ),
        State(
            id = 2,
            name = "Kerala",
            description = "God's Own Country, serene backwaters, and lush hill stations.",
            imageUrl = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=800&q=80"
        ),
        State(
            id = 3,
            name = "Goa",
            description = "Sun, sand, spices, and spectacular beach vibes.",
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80"
        ),
        State(
            id = 4,
            name = "Himachal Pradesh",
            description = "Abode of snow, majestic peaks, and scenic valleys.",
            imageUrl = "https://images.unsplash.com/photo-1605649487212-47bdab064df7?auto=format&fit=crop&w=800&q=80"
        ),
        State(
            id = 5,
            name = "Ladakh",
            description = "The Land of High Passes, stark mountains, and mystical lakes.",
            imageUrl = "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?auto=format&fit=crop&w=800&q=80"
        ),
        State(
            id = 6,
            name = "Tamil Nadu",
            description = "Land of majestic temples, rich culture, and classic traditions.",
            imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80"
        )
    )

    fun blogs(): List<Blog> = listOf(
        Blog(
            id = 1,
            title = "The Golden Mirage: Sunset Over Jaisalmer's Sand Dunes",
            subtitle = "A journey into the heart of the Thar desert, where history sleeps in golden sands.",
            authorName = "Aarav Mehta",
            publishedDate = System.currentTimeMillis() - 86400000 * 2,
            readTimeMin = 6,
            category = "Hidden Gems",
            stateName = "Rajasthan",
            featuredImageUrl = "https://images.unsplash.com/photo-1599661046289-e31897846e41?auto=format&fit=crop&w=800&q=80",
            content = "Deep in the heart of Rajasthan's Thar Desert lies the walled city of Jaisalmer...",
            tagsRaw = "Rajasthan;Desert;CamelSafari;TravelGuide;Sunset",
            isPublished = true,
            isFeatured = true,
            isTrending = true,
            viewCount = 340
        ),
        Blog(
            id = 2,
            title = "Where Time Stands Still: Cruising the Emerald Backwaters of Kerala",
            subtitle = "Sailing through coconut groves and tranquil canals on a traditional Kettuvallam.",
            authorName = "Ananya Nair",
            publishedDate = System.currentTimeMillis() - 86400000 * 5,
            readTimeMin = 5,
            category = "Local Experiences",
            stateName = "Kerala",
            featuredImageUrl = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=800&q=80",
            content = "In the southern state of Kerala, life runs on a different clock...",
            tagsRaw = "Kerala;Backwaters;Houseboat;FoodCuisine;Nature",
            isPublished = true,
            isFeatured = true,
            isEditorsPick = true,
            viewCount = 280
        ),
        Blog(
            id = 3,
            title = "Mystic Chants & Sacred Waters: Spiritual Vibrations in Varanasi",
            subtitle = "A traveler's guide to witnessing the cosmic Ganga Aarti.",
            authorName = "Rohan Sharma",
            publishedDate = System.currentTimeMillis() - 86400000 * 10,
            readTimeMin = 8,
            category = "Spiritual Places",
            stateName = "Tamil Nadu",
            featuredImageUrl = "https://images.unsplash.com/photo-1561361062-12870953fc41?auto=format&fit=crop&w=800&q=80",
            content = "Varanasi, or Kashi, is one of the oldest continuously inhabited cities on Earth...",
            tagsRaw = "Varanasi;Ganges;Spiritual;IndiaCulture;Temples",
            isPublished = true,
            isTrending = true,
            isEditorsPick = true,
            viewCount = 412
        ),
        Blog(
            id = 4,
            title = "Beyond the Snow Peaks: A Guide to Ladakh's High Mountain Passes",
            subtitle = "Riding across Khardung La and camping by the mystical blue waters of Pangong Tso.",
            authorName = "Kabir Singh",
            publishedDate = System.currentTimeMillis() - 86400000 * 15,
            readTimeMin = 7,
            category = "Mountains",
            stateName = "Ladakh",
            featuredImageUrl = "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?auto=format&fit=crop&w=800&q=80",
            content = "Ladakh, often called 'The Land of High Passes', is a cold desert landscape...",
            tagsRaw = "Ladakh;Adventure;Himalayas;RoadTrip;Nature",
            isPublished = true,
            isTrending = true,
            viewCount = 195
        ),
        Blog(
            id = 5,
            title = "Spices & Sizzle: A Culinary Trail Through Old Delhi's Chawri Bazar",
            subtitle = "Savoring centuries-old recipes, melting jalebis, and fiery street food.",
            authorName = "Priya Gupta",
            publishedDate = System.currentTimeMillis() - 86400000 * 20,
            readTimeMin = 4,
            category = "Food & Cuisine",
            stateName = "Himachal Pradesh",
            featuredImageUrl = "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?auto=format&fit=crop&w=800&q=80",
            content = "To truly understand Delhi's history, you must taste it...",
            tagsRaw = "Delhi;StreetFood;IndianSpices;CulinaryTrail;Mughlai",
            isPublished = true,
            isEditorsPick = true,
            viewCount = 310
        )
    )
}
