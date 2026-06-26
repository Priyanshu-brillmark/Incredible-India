package com.example.data

import com.example.data.models.BlogEntity
import com.example.data.models.BookmarkEntity
import com.example.data.models.CategoryEntity
import com.example.data.models.StateEntity
import com.example.data.models.DownloadEntity
import com.example.data.models.ReviewEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BlogRepository(private val blogDao: BlogDao) {

    val allBlogs: Flow<List<BlogEntity>> = blogDao.getAllBlogsFlow()
    val publishedBlogs: Flow<List<BlogEntity>> = blogDao.getPublishedBlogsFlow()
    val categories: Flow<List<CategoryEntity>> = blogDao.getAllCategoriesFlow()
    val states: Flow<List<StateEntity>> = blogDao.getAllStatesFlow()
    val bookmarkedBlogs: Flow<List<BlogEntity>> = blogDao.getBookmarkedBlogsFlow()
    val downloadedBlogs: Flow<List<BlogEntity>> = blogDao.getDownloadedBlogsFlow()

    fun getBlogByIdFlow(id: Int): Flow<BlogEntity?> = blogDao.getBlogByIdFlow(id)

    suspend fun getBlogById(id: Int): BlogEntity? = withContext(Dispatchers.IO) {
        blogDao.getBlogById(id)
    }

    suspend fun insertBlog(blog: BlogEntity): Long = withContext(Dispatchers.IO) {
        blogDao.insertBlog(blog)
    }

    suspend fun updateBlog(blog: BlogEntity) = withContext(Dispatchers.IO) {
        blogDao.updateBlog(blog)
    }

    suspend fun deleteBlog(id: Int) = withContext(Dispatchers.IO) {
        blogDao.deleteBlogById(id)
    }

    suspend fun incrementViewCount(id: Int) = withContext(Dispatchers.IO) {
        blogDao.incrementViewCount(id)
    }

    // --- CATEGORIES ---
    suspend fun insertCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        blogDao.insertCategory(category)
    }

    suspend fun deleteCategory(id: Int) = withContext(Dispatchers.IO) {
        blogDao.deleteCategoryById(id)
    }

    // --- STATES ---
    suspend fun insertState(state: StateEntity) = withContext(Dispatchers.IO) {
        blogDao.insertState(state)
    }

    suspend fun deleteState(id: Int) = withContext(Dispatchers.IO) {
        blogDao.deleteStateById(id)
    }

    // --- BOOKMARKS ---
    suspend fun toggleBookmark(blogId: Int) = withContext(Dispatchers.IO) {
        val exists = blogDao.isBookmarked(blogId)
        if (exists) {
            blogDao.deleteBookmark(blogId)
        } else {
            blogDao.insertBookmark(BookmarkEntity(blogId = blogId))
        }
    }

    fun isBookmarkedFlow(blogId: Int): Flow<Boolean> = blogDao.isBookmarkedFlow(blogId)

    // --- DOWNLOADS (OFFLINE BLOGS) ---
    suspend fun toggleDownload(blogId: Int) = withContext(Dispatchers.IO) {
        val exists = blogDao.isDownloaded(blogId)
        if (exists) {
            blogDao.deleteDownload(blogId)
        } else {
            blogDao.insertDownload(DownloadEntity(blogId = blogId))
        }
    }

    fun isDownloadedFlow(blogId: Int): Flow<Boolean> = blogDao.isDownloadedFlow(blogId)

    // --- REVIEWS & RATINGS ---
    fun getReviewsForBlogFlow(blogId: Int): Flow<List<ReviewEntity>> = blogDao.getReviewsForBlogFlow(blogId)

    suspend fun addReview(review: ReviewEntity) = withContext(Dispatchers.IO) {
        blogDao.insertReview(review)
    }

    suspend fun deleteReview(id: Int) = withContext(Dispatchers.IO) {
        blogDao.deleteReview(id)
    }

    // --- SEED DATABASE ---
    suspend fun seedDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        val existingCategories = blogDao.getAllCategories()
        if (existingCategories.isEmpty()) {
            seedCategories()
        }

        val existingStates = blogDao.getAllStates()
        if (existingStates.isEmpty()) {
            seedStates()
        }

        // Check if blog list is empty, if so, seed blogs
        val allCurrentBlogs = blogDao.getAllCategories() // just a check, we can check any
        // Let's count existing blogs
        val blogCheck = blogDao.getBlogById(1)
        if (blogCheck == null) {
            seedInitialBlogs()
        }
    }

    private suspend fun seedCategories() {
        val initialCategories = listOf(
            CategoryEntity(name = "Festivals", iconName = "celebration"),
            CategoryEntity(name = "Hidden Gems", iconName = "explore"),
            CategoryEntity(name = "Culture & Heritage", iconName = "museum"),
            CategoryEntity(name = "Food & Cuisine", iconName = "restaurant"),
            CategoryEntity(name = "Adventure", iconName = "terrain"),
            CategoryEntity(name = "Wildlife", iconName = "pets"),
            CategoryEntity(name = "Beaches", iconName = "beach_access"),
            CategoryEntity(name = "Mountains", iconName = "filter_hdr"),
            CategoryEntity(name = "Spiritual Places", iconName = "spa"),
            CategoryEntity(name = "Historical Monuments", iconName = "account_balance"),
            CategoryEntity(name = "Road Trips", iconName = "directions_car"),
            CategoryEntity(name = "Shopping", iconName = "shopping_bag"),
            CategoryEntity(name = "Local Experiences", iconName = "groups")
        )
        initialCategories.forEach { blogDao.insertCategory(it) }
    }

    private suspend fun seedStates() {
        val initialStates = listOf(
            StateEntity(
                name = "Rajasthan",
                description = "The Land of Kings, majestic forts, and golden deserts.",
                imageUrl = "https://images.unsplash.com/photo-1599661046289-e31897846e41?auto=format&fit=crop&w=800&q=80"
            ),
            StateEntity(
                name = "Kerala",
                description = "God's Own Country, serene backwaters, and lush hill stations.",
                imageUrl = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=800&q=80"
            ),
            StateEntity(
                name = "Goa",
                description = "Sun, sand, spices, and spectacular beach vibes.",
                imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80"
            ),
            StateEntity(
                name = "Himachal Pradesh",
                description = "Abode of snow, majestic peaks, and scenic valleys.",
                imageUrl = "https://images.unsplash.com/photo-1605649487212-47bdab064df7?auto=format&fit=crop&w=800&q=80"
            ),
            StateEntity(
                name = "Ladakh",
                description = "The Land of High Passes, stark mountains, and mystical lakes.",
                imageUrl = "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?auto=format&fit=crop&w=800&q=80"
            ),
            StateEntity(
                name = "Tamil Nadu",
                description = "Land of majestic temples, rich culture, and classic traditions.",
                imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80"
            )
        )
        initialStates.forEach { blogDao.insertState(it) }
    }

    private suspend fun seedInitialBlogs() {
        val initialBlogs = listOf(
            BlogEntity(
                id = 1,
                title = "The Golden Mirage: Sunset Over Jaisalmer's Sand Dunes",
                subtitle = "A journey into the heart of the Thar desert, where history sleeps in golden sands.",
                authorName = "Aarav Mehta",
                publishedDate = System.currentTimeMillis() - 86400000 * 2, // 2 days ago
                readTimeMin = 6,
                category = "Hidden Gems",
                stateName = "Rajasthan",
                featuredImageUrl = "https://images.unsplash.com/photo-1599661046289-e31897846e41?auto=format&fit=crop&w=800&q=80",
                content = """
                    Deep in the heart of Rajasthan's Thar Desert lies the walled city of Jaisalmer, known lovingly as the 'Golden City' due to the yellow sandstone architecture that glows like honey under the midday sun. While the imposing Jaisalmer Fort is a wonder in itself, the true magic of this region is found further west, amidst the undulating sand dunes of Sam and Khuri.
                    
                    ### Journeying to the Dunes
                    As you drive away from Jaisalmer, the sparse scrublands give way to vast expanses of ripples and ridges. The dry wind carries with it ancient whispers of caravanners who once traveled the historic Silk Route. 
                    
                    We opted for a camel safari in the late afternoon. Riding on these gentle giants, we watched the shadows grow long and blue across the crests of the dunes. The silence of the desert is absolute, interrupted only by the soft, rhythmic padding of the camels.
                    
                    ### The Sunset Spectacle
                    By 5:30 PM, we reached a secluded ridge far from the commercial camps. The sun began its descent, transforming the sky into a canvas of fiery orange, deep magenta, and dusty gold. The sand beneath us, still warm from the day's heat, seemed to mirror the colors of the heavens. It is a moment of pure serenity, watching the sun dip below the horizon line of an infinite desert.
                    
                    ### Folk Music Under the Stars
                    As twilight settled, we made our way to an open camp. Around a crackling bonfire, local Manganiyar musicians tuned their traditional instruments—the Kamayacha and Khartal. Their haunting, powerful voices rose into the crisp desert night, singing songs of love, longing, and historical battles. Under a canopy of a million stars, with no city lights in sight, the melodies made the ancient desert come alive.
                    
                    ### Practical Tips
                    - **Best Time to Visit:** October to March, when daytime temperatures are pleasant.
                    - **Stargazing:** Carry a warm jacket; the desert cools down rapidly after sunset.
                    - **Eco-Awareness:** Please do not litter in the dunes; preserve this fragile environment.
                """.trimIndent(),
                tagsRaw = "Rajasthan;Desert;CamelSafari;TravelGuide;Sunset",
                galleryRaw = "https://images.unsplash.com/photo-1599661046289-e31897846e41?auto=format&fit=crop&w=800&q=80;https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80",
                isPublished = true,
                isFeatured = true,
                isTrending = true,
                isEditorsPick = false,
                seoTitle = "Jaisalmer Desert Safari Travel Blog | Incredible India",
                seoDescription = "Read an immersive travel story about witnessing the glorious sunset over Jaisalmer's golden Thar sand dunes in Rajasthan.",
                viewCount = 340
            ),
            BlogEntity(
                id = 2,
                title = "Where Time Stands Still: Cruising the Emerald Backwaters of Kerala",
                subtitle = "Sailing through coconut groves and tranquil canals on a traditional Kettuvallam.",
                authorName = "Ananya Nair",
                publishedDate = System.currentTimeMillis() - 86400000 * 5, // 5 days ago
                readTimeMin = 5,
                category = "Local Experiences",
                stateName = "Kerala",
                featuredImageUrl = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=800&q=80",
                content = """
                    In the southern state of Kerala, life runs on a different clock—one measured by the gentle flow of water and the swaying of coconut palms. The backwaters, a labyrinthine network of interconnected brackish lagoons, lakes, and canals running parallel to the Arabian Sea, are a unique ecosystem that defines Kerala's identity.
                    
                    ### Boarding the Kettuvallam
                    Our journey started in Alappuzha (formerly Alleppey), the gateway to the backwaters. Here, we boarded a beautifully restored *Kettuvallam*—a traditional houseboat made completely of natural materials like jackwood, coir rope, and bamboo poles, sewn together without a single nail.
                    
                    Stepping on board felt like stepping into an elegant, floating cottage. Complete with private bedrooms, modern en-suite bathrooms, and a spacious open-deck lounge, it was the perfect vessel to glide slowly through paradise.
                    
                    ### A Liquid Highway
                    As the boat captain steered us away from the bustling jetty, we entered a peaceful world. The water was smooth as glass, reflecting the intense greenery of the banks. Towering coconut trees leaned gracefully over the canals, forming green arches overhead.
                    
                    We passed small villages where children waved from the banks, women washed vessels, and men cast fishing nets from tiny canoes. Duck farming is popular here, and we frequently sailed past flocks of hundreds of white ducks paddling together in perfect coordination.
                    
                    ### Savoring Backwater Flavors
                    No backwater cruise is complete without experiencing the local cuisine. Our onboard chef prepared lunch using fresh ingredients bought directly from local fishermen. 
                    
                    We feasted on *Karimeen Pollichathu* (pearl spot fish marinated in fiery spices, wrapped in banana leaves, and pan-fried), served alongside red Matta rice, Avial (mixed vegetables in coconut paste), and sweet banana fritters for dessert. The fresh coconut water served straight from the shell kept us cool under the afternoon sun.
                    
                    ### Nightfall on the Lake
                    As evening approached, the captain anchored the boat in the middle of Vembanad Lake. The sky softened into pastel shades of lavender and pink. As the distant lights of lakeside villages began to twinkle, we listened to the quiet lap of waves against the hull. It was a profound silence that stayed with us long after we stepped back onto dry land.
                """.trimIndent(),
                tagsRaw = "Kerala;Backwaters;Houseboat;FoodCuisine;Nature",
                galleryRaw = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=800&q=80",
                isPublished = true,
                isFeatured = true,
                isTrending = false,
                isEditorsPick = true,
                seoTitle = "Kerala Backwaters Houseboat Travel Blog",
                seoDescription = "Experience the serenity of cruising the emerald canals of Alappuzha, Kerala on a luxury houseboat.",
                viewCount = 280
            ),
            BlogEntity(
                id = 3,
                title = "Mystic Chants & Sacred Waters: Spiritual Vibrations in Varanasi",
                subtitle = "A traveler's guide to witnessing the cosmic Ganga Aarti and exploring the narrow alleys.",
                authorName = "Rohan Sharma",
                publishedDate = System.currentTimeMillis() - 86400000 * 10,
                readTimeMin = 8,
                category = "Spiritual Places",
                stateName = "Tamil Nadu", // Or map to generic Spiritual Places
                featuredImageUrl = "https://images.unsplash.com/photo-1561361062-12870953fc41?auto=format&fit=crop&w=800&q=80",
                content = """
                    Varanasi, or Kashi, is one of the oldest continuously inhabited cities on Earth. To Hindus, it is the center of the spiritual universe—a place where life and death dance openly on the banks of the sacred River Ganges. Visiting Varanasi is not just a holiday; it is an intense sensory and spiritual immersion.
                    
                    ### The Morning Row
                    The best way to witness Varanasi's soul is from a wooden rowing boat at sunrise. At 5:00 AM, the air is cool and mist floats over the sacred water. 
                    
                    As we rowed past the ghats—the series of stone steps leading down to the river—the city was already wide awake. Pilgrims were taking their holy dips, priests were meditating in yogic postures, and colorful sarees were spread out to dry on the stone steps like giant works of art. The orange sun rose majestically across the river, painting the historical temples in shades of crimson and gold.
                    
                    ### The Alleys of Kashi
                    Behind the open riverfront lies a dense maze of *galis* (narrow alleys) so tight that sunlight barely touches the cobblestones. Walking through them is an adventure: you share the path with temple-goers, roaming cows, street vendors selling hot samosas, and shops displaying shining silk sarees.
                    
                    Every turn reveals a hidden shrine, the smell of fresh sandalwood, or a vendor boiling rich milk tea in clay cups called *kulhads*. 
                    
                    ### The Spectacular Ganga Aarti
                    As dusk falls, thousands gather at Dashashwamedh Ghat for the world-famous Ganga Aarti. Seven young priests dressed in saffron dhotis stand on wooden platforms facing the river. 
                    
                    With absolute precision, synchronized to the ringing of bells, blowing of conch shells, and chanting of Sanskrit hymns, they wave heavy, multi-layered brass lamps blazing with fire. The smoke, the light, and the spiritual energy of thousands of people clapping in unison create an atmosphere that feels almost otherworldly. Tiny leaf-boats holding oil lamps and flowers are released into the river, turning the Ganges into a floating galaxy of light.
                """.trimIndent(),
                tagsRaw = "Varanasi;Ganges;Spiritual;IndiaCulture;Temples",
                galleryRaw = "https://images.unsplash.com/photo-1561361062-12870953fc41?auto=format&fit=crop&w=800&q=80",
                isPublished = true,
                isFeatured = false,
                isTrending = true,
                isEditorsPick = true,
                seoTitle = "Varanasi Ganga Aarti & Ghats Guide",
                seoDescription = "An immersive travel blog about experiencing the spiritual energy, morning boat rides, and the mesmerizing Ganga Aarti in Varanasi.",
                viewCount = 412
            ),
            BlogEntity(
                id = 4,
                title = "Beyond the Snow Peaks: A Guide to Ladakh's High Mountain Passes",
                subtitle = "Riding across Khardung La and camping by the mystical blue waters of Pangong Tso.",
                authorName = "Kabir Singh",
                publishedDate = System.currentTimeMillis() - 86400000 * 15,
                readTimeMin = 7,
                category = "Mountains",
                stateName = "Ladakh",
                featuredImageUrl = "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?auto=format&fit=crop&w=800&q=80",
                content = """
                    Ladakh, often called 'The Land of High Passes', is a cold desert landscape of dramatic contrast. Here, snow-capped jagged peaks rise from barren, brown valleys, and the silence is punctuated only by the flutter of Buddhist prayer flags sending goodwill into the mountain wind.
                    
                    ### Crossing the Khardung La
                    Our adventure began in Leh, the capital of Ladakh, situated at 11,500 feet. After spending two days acclimatizing to the thin air, we rode out towards the Khardung La pass—one of the highest motorable roads in the world at an altitude of 17,582 feet.
                    
                    The climb is steep and treacherous, winding through rocky paths and patches of pure ice. Reaching the summit is a triumph. Standing amidst colorful prayer flags, drinking a hot cup of tea at the small army-run cafeteria, and looking down at the Karakoram range stretching into infinity is an unforgettable experience.
                    
                    ### The Blue Magic of Pangong Tso
                    From the high passes, we descended into the vast Changthang plateau towards Pangong Tso, a 134 km long endorheic lake that spans India and Tibet.
                    
                    As you round the final mountain bend, the lake appears as a thin strip of shocking, bright turquoise water framed by dry, golden peaks. The water is incredibly clear, and its colors shift dynamically from pale light blue to deep indigo and emerald throughout the day as the sun moves across the sky.
                    
                    Camping in luxury tents by the lakeside under a crisp, freezing night sky reveals a celestial map so clear that the Milky Way is visible to the naked eye.
                """.trimIndent(),
                tagsRaw = "Ladakh;Adventure;Himalayas;RoadTrip;Nature",
                galleryRaw = "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?auto=format&fit=crop&w=800&q=80",
                isPublished = true,
                isFeatured = false,
                isTrending = true,
                isEditorsPick = false,
                seoTitle = "Ladakh Road Trip Khardung La & Pangong Lake Guide",
                seoDescription = "Ultimate traveler's guide to Ladakh, crossing high mountain passes like Khardung La and camping by Pangong Tso.",
                viewCount = 195
            ),
            BlogEntity(
                id = 5,
                title = "Spices & Sizzle: A Culinary Trail Through Old Delhi's Chawri Bazar",
                subtitle = "Savoring centuries-old recipes, melting jalebis, and fiery street food.",
                authorName = "Priya Gupta",
                publishedDate = System.currentTimeMillis() - 86400000 * 20,
                readTimeMin = 4,
                category = "Food & Cuisine",
                stateName = "Himachal Pradesh", // Or Maharashtra / etc.
                featuredImageUrl = "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?auto=format&fit=crop&w=800&q=80",
                content = """
                    To truly understand Delhi's history, you must taste it. Old Delhi, or Shahjahanabad, is a culinary goldmine where recipes have been passed down through generations of cooks since the Mughal era. Our culinary journey began in the crowded, sensory-overload lanes of Chandni Chowk and Chawri Bazar.
                    
                    ### Paranthe Wali Gali
                    This legendary lane has been serving deep-fried stuffed flatbreads (paranthas) since the 1870s. We visited Babu Ram Paranthe Wale and ordered paranthas stuffed with unexpected ingredients like green peas, mixed spices, and even crushed cashew nuts. Fried in pure ghee in giant iron woks, they are served hot and crispy with spicy potato curry, sweet pumpkin mash, and mint chutney.
                    
                    ### The Sweet Secrets
                    Next, we stopped at Old Famous Jalebi Wala, which has been frying thick, syrupy jalebis since 1884. Unlike the thin, crispy versions found elsewhere, these are thick, soft, juicy, and fried over charcoal, giving them a distinct smoky flavor. Savoring them hot, paired with a scoop of cool rabri (sweet condensed milk), is pure culinary ecstasy.
                    
                    ### Slow-Cooked Mughlai Masterpieces
                    For mains, we headed towards the shadow of the historic Jama Masjid to taste slow-cooked stews like *Nihari*—beef or lamb slow-cooked overnight with bone marrow and a secret blend of fifty spices, resulting in meat so tender it falls off the bone. Paired with hot, pillowy *Khameeri Roti* straight from the clay tandoor oven, it is a meal fit for royalty.
                """.trimIndent(),
                tagsRaw = "Delhi;StreetFood;IndianSpices;CulinaryTrail;Mughlai",
                galleryRaw = "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?auto=format&fit=crop&w=800&q=80",
                isPublished = true,
                isFeatured = false,
                isTrending = false,
                isEditorsPick = true,
                seoTitle = "Old Delhi Culinary & Street Food Guide",
                seoDescription = "Explore the famous street food, sweet shops, and Mughlai slow-cooked dishes of Chandni Chowk in Old Delhi.",
                viewCount = 310
            )
        )
        initialBlogs.forEach { blogDao.insertBlog(it) }
    }
}
