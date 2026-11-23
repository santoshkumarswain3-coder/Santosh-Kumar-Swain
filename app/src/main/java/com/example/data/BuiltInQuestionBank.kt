package com.example.data

import com.example.R
import com.example.data.model.QuestionCategory
import com.example.data.model.QuizQuestion

object BuiltInQuestionBank {
    val questions: List<QuizQuestion> = listOf(
        // --- CURRENT AFFAIRS WITH PICTURES (1-10) ---
        QuizQuestion(
            id = "ca_1",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "Which space agency achieved the historic Chandrayaan-3 soft landing near the Moon's south pole?",
            options = listOf("NASA", "ISRO", "ESA", "JAXA"),
            correctIndex = 1,
            explanation = "India's ISRO successfully landed Chandrayaan-3 near the lunar south pole in August 2023.",
            localImageRes = R.drawable.img_current_affairs_space
        ),
        QuizQuestion(
            id = "ca_2",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "Which clean energy sources shown in eco-cities convert sunlight and moving air directly into electricity?",
            options = listOf("Coal and oil", "Solar panels and wind turbines", "Diesel generators", "Natural gas boilers"),
            correctIndex = 1,
            explanation = "Solar photovoltaic panels and wind turbines generate renewable electricity without polluting smoke.",
            localImageRes = R.drawable.img_current_affairs_nature
        ),
        QuizQuestion(
            id = "ca_3",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "What is the name of the NASA program aimed at returning astronauts to the Moon and preparing for Mars?",
            options = listOf("Voyager", "Artemis", "Hubble", "Pioneer"),
            correctIndex = 1,
            explanation = "The Artemis program leads modern human lunar exploration.",
            localImageRes = R.drawable.img_current_affairs_space
        ),
        QuizQuestion(
            id = "ca_4",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "Why do modern green cities plant rooftop gardens and preserve river corridors?",
            options = listOf("To block sunlight", "To cool cities, clean air, and support wildlife", "To remove footpaths", "To reduce rain"),
            correctIndex = 1,
            explanation = "Urban greenery lowers heat, absorbs carbon dioxide, and creates habitats for birds and wildlife.",
            localImageRes = R.drawable.img_current_affairs_nature
        ),
        QuizQuestion(
            id = "ca_5",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "Which powerful space telescope orbits far beyond the Moon to capture infrared images of early galaxies?",
            options = listOf("James Webb Space Telescope", "Galileo Spyglass", "Sputnik 1", "Cassini Probe"),
            correctIndex = 0,
            explanation = "The James Webb Space Telescope (JWST) observes distant galaxies in infrared light.",
            localImageRes = R.drawable.img_current_affairs_space
        ),
        QuizQuestion(
            id = "ca_6",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "Which international agreement brings countries together to limit global warming and protect our climate?",
            options = listOf("Paris Agreement", "Magna Carta", "Olympic Charter", "Maritime Law"),
            correctIndex = 0,
            explanation = "The Paris Agreement unites nations to cut greenhouse gas emissions and adopt clean energy.",
            localImageRes = R.drawable.img_current_affairs_nature
        ),
        QuizQuestion(
            id = "ca_7",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "Which Indian solar observatory mission studies the Sun from the L1 Lagrange point?",
            options = listOf("Aditya-L1", "Mangalyaan-2", "Gaganyaan", "Vikram-S"),
            correctIndex = 0,
            explanation = "Aditya-L1 is India's first dedicated space-based solar mission.",
            localImageRes = R.drawable.img_current_affairs_space
        ),
        QuizQuestion(
            id = "ca_8",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "World Environment Day is celebrated around the globe every year on which date?",
            options = listOf("April 1", "June 5", "August 15", "December 25"),
            correctIndex = 1,
            explanation = "June 5 is observed worldwide as World Environment Day.",
            localImageRes = R.drawable.img_current_affairs_nature
        ),
        QuizQuestion(
            id = "ca_9",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "Which continent hosted the 2024 Summer Olympic Games in Paris?",
            options = listOf("Asia", "Europe", "South America", "Africa"),
            correctIndex = 1,
            explanation = "Paris, France is located in Europe and hosted the 2024 Summer Olympics."
        ),
        QuizQuestion(
            id = "ca_10",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "What does 'AI' stand for in modern computer science and robotics?",
            options = listOf("Automated Internet", "Artificial Intelligence", "Aerial Imaging", "Applied Index"),
            correctIndex = 1,
            explanation = "AI stands for Artificial Intelligence — smart software that helps solve problems and answer questions."
        ),

        // --- SCIENCE & SPACE (11-24) ---
        QuizQuestion(
            id = "sc_11",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "Which planet in our solar system is famous for its bright icy rings?",
            options = listOf("Mercury", "Saturn", "Mars", "Venus"),
            correctIndex = 1,
            explanation = "Saturn has the largest and brightest ring system made of ice and rock particles.",
            localImageRes = R.drawable.img_current_affairs_space
        ),
        QuizQuestion(
            id = "sc_12",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "What gas do green plants absorb from the air to make food during photosynthesis?",
            options = listOf("Carbon dioxide", "Nitrogen", "Helium", "Argon"),
            correctIndex = 0,
            explanation = "Plants use sunlight, water, and carbon dioxide to produce glucose and release oxygen.",
            localImageRes = R.drawable.img_current_affairs_nature
        ),
        QuizQuestion(
            id = "sc_13",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "How many planets are officially recognized in our solar system?",
            options = listOf("7", "8", "9", "10"),
            correctIndex = 1,
            explanation = "There are 8 planets: Mercury, Venus, Earth, Mars, Jupiter, Saturn, Uranus, and Neptune."
        ),
        QuizQuestion(
            id = "sc_14",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "Which organ pumps blood to every part of the human body?",
            options = listOf("Liver", "Heart", "Kidney", "Stomach"),
            correctIndex = 1,
            explanation = "The heart is a muscular organ that circulates oxygen-rich blood throughout the body."
        ),
        QuizQuestion(
            id = "sc_15",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "What is the boiling point of pure water at sea level?",
            options = listOf("50°C", "90°C", "100°C", "120°C"),
            correctIndex = 2,
            explanation = "At standard atmospheric pressure, pure water boils at 100 degrees Celsius (212°F)."
        ),
        QuizQuestion(
            id = "sc_16",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "Which is the largest planet in our solar system?",
            options = listOf("Earth", "Jupiter", "Neptune", "Mars"),
            correctIndex = 1,
            explanation = "Jupiter is a gas giant and more than twice as massive as all other planets combined."
        ),
        QuizQuestion(
            id = "sc_17",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "Which vitamin does human skin naturally make when exposed to gentle morning sunlight?",
            options = listOf("Vitamin A", "Vitamin C", "Vitamin D", "Vitamin K"),
            correctIndex = 2,
            explanation = "Sunlight triggers Vitamin D production, which keeps bones and teeth strong."
        ),
        QuizQuestion(
            id = "sc_18",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "What force keeps the Moon in orbit around Earth and keeps our feet on the ground?",
            options = listOf("Magnetism", "Friction", "Gravity", "Electricity"),
            correctIndex = 2,
            explanation = "Gravity pulls objects with mass toward each other and holds planets and moons in orbit."
        ),
        QuizQuestion(
            id = "sc_19",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "Which metal is liquid at room temperature and used in some classic thermometers?",
            options = listOf("Iron", "Mercury", "Copper", "Aluminium"),
            correctIndex = 1,
            explanation = "Mercury is the only metallic element that is liquid at standard room temperature."
        ),
        QuizQuestion(
            id = "sc_20",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "What part of a flower produces pollen?",
            options = listOf("Root", "Anther", "Stem", "Petal"),
            correctIndex = 1,
            explanation = "The anther, part of the stamen, produces pollen grains."
        ),
        QuizQuestion(
            id = "sc_21",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "Approximately how long does it take light from the Sun to reach Earth?",
            options = listOf("8 seconds", "8 minutes", "8 hours", "8 days"),
            correctIndex = 1,
            explanation = "Sunlight travels about 150 million km to reach Earth in roughly 8 minutes and 20 seconds."
        ),
        QuizQuestion(
            id = "sc_22",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "Which animal is the largest mammal on Earth?",
            options = listOf("African Elephant", "Blue Whale", "Giraffe", "Polar Bear"),
            correctIndex = 1,
            explanation = "The Blue Whale is the largest animal known to have ever lived on Earth."
        ),
        QuizQuestion(
            id = "sc_23",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "What instrument is used to view tiny cells and microbes that are invisible to the naked eye?",
            options = listOf("Telescope", "Microscope", "Barometer", "Compass"),
            correctIndex = 1,
            explanation = "A microscope magnifies tiny objects such as plant cells and bacteria."
        ),
        QuizQuestion(
            id = "sc_24",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "Which gas makes up about 78% of Earth's atmosphere?",
            options = listOf("Oxygen", "Carbon dioxide", "Nitrogen", "Hydrogen"),
            correctIndex = 2,
            explanation = "Earth's air is roughly 78% nitrogen, 21% oxygen, and 1% other gases."
        ),

        // --- GEOGRAPHY (25-36) ---
        QuizQuestion(
            id = "geo_25",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which is the largest ocean on Earth?",
            options = listOf("Atlantic Ocean", "Indian Ocean", "Pacific Ocean", "Arctic Ocean"),
            correctIndex = 2,
            explanation = "The Pacific Ocean covers about one-third of Earth's surface."
        ),
        QuizQuestion(
            id = "geo_26",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which continent is home to the Sahara, the world's largest hot desert?",
            options = listOf("Asia", "Africa", "Australia", "South America"),
            correctIndex = 1,
            explanation = "The Sahara Desert spans much of northern Africa."
        ),
        QuizQuestion(
            id = "geo_27",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "What is the capital city of Japan?",
            options = listOf("Kyoto", "Osaka", "Tokyo", "Seoul"),
            correctIndex = 2,
            explanation = "Tokyo has been Japan's capital since 1868."
        ),
        QuizQuestion(
            id = "geo_28",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which mountain peak is the highest point above sea level on Earth?",
            options = listOf("K2", "Mount Everest", "Kangchenjunga", "Mount Kilimanjaro"),
            correctIndex = 1,
            explanation = "Mount Everest in the Himalayas rises 8,848.86 meters above sea level."
        ),
        QuizQuestion(
            id = "geo_29",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which river flows through Egypt and is among the longest rivers in the world?",
            options = listOf("Amazon", "Nile", "Ganga", "Danube"),
            correctIndex = 1,
            explanation = "The Nile River flows northward through northeastern Africa into the Mediterranean Sea."
        ),
        QuizQuestion(
            id = "geo_30",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which country is both a country and a continent?",
            options = listOf("Brazil", "Canada", "Australia", "India"),
            correctIndex = 2,
            explanation = "Australia is the world's smallest continent and a single nation."
        ),
        QuizQuestion(
            id = "geo_31",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "What is the capital city of India?",
            options = listOf("Mumbai", "New Delhi", "Bengaluru", "Kolkata"),
            correctIndex = 1,
            explanation = "New Delhi is the capital city of India."
        ),
        QuizQuestion(
            id = "geo_32",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which imaginary line divides Earth into the Northern and Southern Hemispheres?",
            options = listOf("Prime Meridian", "Tropic of Cancer", "Equator", "Arctic Circle"),
            correctIndex = 2,
            explanation = "The Equator sits at 0 degrees latitude around Earth's middle."
        ),
        QuizQuestion(
            id = "geo_33",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which rainforest is often called the 'lungs of the Earth'?",
            options = listOf("Congo Rainforest", "Amazon Rainforest", "Daintree Rainforest", "Sundarbans"),
            correctIndex = 1,
            explanation = "The Amazon Rainforest in South America is the largest tropical rainforest on Earth.",
            localImageRes = R.drawable.img_current_affairs_nature
        ),
        QuizQuestion(
            id = "geo_34",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which country is known as the 'Land of the Rising Sun'?",
            options = listOf("Thailand", "Japan", "Norway", "New Zealand"),
            correctIndex = 1,
            explanation = "Japan's name (Nihon/Nippon) literally means 'origin of the sun'."
        ),
        QuizQuestion(
            id = "geo_35",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which continent has no permanent human population and is covered mostly in ice?",
            options = listOf("Europe", "Antarctica", "South America", "Africa"),
            correctIndex = 1,
            explanation = "Antarctica hosts scientific research stations rather than permanent cities."
        ),
        QuizQuestion(
            id = "geo_36",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "How many continents are there on Earth?",
            options = listOf("5", "6", "7", "8"),
            correctIndex = 2,
            explanation = "Asia, Africa, North America, South America, Antarctica, Europe, and Australia make 7."
        ),

        // --- HISTORY & CIVICS (37-48) ---
        QuizQuestion(
            id = "his_37",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Who was the first Prime Minister of independent India?",
            options = listOf("Sardar Vallabhbhai Patel", "Jawaharlal Nehru", "Dr. Rajendra Prasad", "Subhas Chandra Bose"),
            correctIndex = 1,
            explanation = "Pandit Jawaharlal Nehru served as India's first Prime Minister from 1947."
        ),
        QuizQuestion(
            id = "his_38",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Who was the first human to step onto the surface of the Moon in 1969?",
            options = listOf("Buzz Aldrin", "Yuri Gagarin", "Neil Armstrong", "Kalpana Chawla"),
            correctIndex = 2,
            explanation = "Neil Armstrong stepped onto the Moon during Apollo 11 on July 20, 1969."
        ),
        QuizQuestion(
            id = "his_39",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Which ancient civilization built the Great Pyramids of Giza?",
            options = listOf("Roman", "Egyptian", "Mesopotamian", "Mayan"),
            correctIndex = 1,
            explanation = "Ancient Egyptians built the Great Pyramids near Cairo over 4,500 years ago."
        ),
        QuizQuestion(
            id = "his_40",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Who is famously known as the 'Father of the Indian Constitution'?",
            options = listOf("Mahatma Gandhi", "Dr. B. R. Ambedkar", "Dr. A. P. J. Abdul Kalam", "Rabindranath Tagore"),
            correctIndex = 1,
            explanation = "Dr. Bhimrao Ramji Ambedkar chaired the Drafting Committee of the Indian Constitution."
        ),
        QuizQuestion(
            id = "his_41",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Who invented the practical incandescent light bulb and phonograph?",
            options = listOf("Thomas Edison", "Isaac Newton", "Albert Einstein", "Galileo Galilei"),
            correctIndex = 0,
            explanation = "Thomas Edison developed the first commercially practical incandescent light bulb."
        ),
        QuizQuestion(
            id = "his_42",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Which renowned scientist discovered the laws of motion and universal gravitation?",
            options = listOf("Charles Darwin", "Sir Isaac Newton", "Marie Curie", "Louis Pasteur"),
            correctIndex = 1,
            explanation = "Sir Isaac Newton formulated the three laws of motion and gravity."
        ),
        QuizQuestion(
            id = "his_43",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "In which year did the United Nations (UN) officially come into existence?",
            options = listOf("1919", "1945", "1955", "1969"),
            correctIndex = 1,
            explanation = "The United Nations was founded on October 24, 1945."
        ),
        QuizQuestion(
            id = "his_44",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Who wrote India's National Anthem 'Jana Gana Mana'?",
            options = listOf("Bankim Chandra Chatterjee", "Rabindranath Tagore", "Sarojini Naidu", "Premchand"),
            correctIndex = 1,
            explanation = "Nobel Laureate Rabindranath Tagore composed 'Jana Gana Mana'."
        ),
        QuizQuestion(
            id = "his_45",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Who was the first woman to win a Nobel Prize?",
            options = listOf("Rosalind Franklin", "Marie Curie", "Florence Nightingale", "Ada Lovelace"),
            correctIndex = 1,
            explanation = "Marie Curie won the Nobel Prize in Physics in 1903 and Chemistry in 1911."
        ),
        QuizQuestion(
            id = "his_46",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Which famous brothers achieved the first powered airplane flight in 1903?",
            options = listOf("Lumiere Brothers", "Wright Brothers", "Grimm Brothers", "Montgolfier Brothers"),
            correctIndex = 1,
            explanation = "Orville and Wilbur Wright flew the first powered aircraft at Kitty Hawk in 1903."
        ),
        QuizQuestion(
            id = "his_47",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "What is the supreme law-making document of a democratic country called?",
            options = listOf("Almanac", "Constitution", "Dictionary", "Atlas"),
            correctIndex = 1,
            explanation = "A Constitution defines citizens' rights, duties, and how government works."
        ),
        QuizQuestion(
            id = "his_48",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Who is celebrated as the 'Missile Man of India' and served as India's 11th President?",
            options = listOf("Dr. C. V. Raman", "Dr. A. P. J. Abdul Kalam", "Vikram Sarabhai", "Homi Bhabha"),
            correctIndex = 1,
            explanation = "Dr. A. P. J. Abdul Kalam inspired millions of students through science and aerospace leadership."
        ),

        // --- SPORTS, ARTS & GENERAL KNOWLEDGE (49-60) ---
        QuizQuestion(
            id = "gen_49",
            category = QuestionCategory.SPORTS_ARTS,
            questionText = "How many players are on a standard cricket team on the field at one time?",
            options = listOf("9", "10", "11", "12"),
            correctIndex = 2,
            explanation = "Each cricket side fields 11 players."
        ),
        QuizQuestion(
            id = "gen_50",
            category = QuestionCategory.SPORTS_ARTS,
            questionText = "How many interlocking rings appear on the Olympic flag?",
            options = listOf("4", "5", "6", "7"),
            correctIndex = 1,
            explanation = "The 5 Olympic rings represent the union of five inhabited continents."
        ),
        QuizQuestion(
            id = "gen_51",
            category = QuestionCategory.SPORTS_ARTS,
            questionText = "Who painted the famous artwork 'Mona Lisa'?",
            options = listOf("Pablo Picasso", "Leonardo da Vinci", "Vincent van Gogh", "Michelangelo"),
            correctIndex = 1,
            explanation = "Italian Renaissance artist Leonardo da Vinci painted the Mona Lisa."
        ),
        QuizQuestion(
            id = "gen_52",
            category = QuestionCategory.SPORTS_ARTS,
            questionText = "Which board game uses pieces named King, Queen, Rook, Bishop, Knight, and Pawn?",
            options = listOf("Checkers", "Chess", "Ludo", "Carrom"),
            correctIndex = 1,
            explanation = "Chess is played on an 8x8 checkered board with these six piece types."
        ),
        QuizQuestion(
            id = "gen_53",
            category = QuestionCategory.GENERAL,
            questionText = "How many colors are visible in a classic rainbow?",
            options = listOf("5", "6", "7", "8"),
            correctIndex = 2,
            explanation = "VIBGYOR: Violet, Indigo, Blue, Green, Yellow, Orange, and Red."
        ),
        QuizQuestion(
            id = "gen_54",
            category = QuestionCategory.GENERAL,
            questionText = "Which is the fastest land animal in the world?",
            options = listOf("Lion", "Cheetah", "Leopard", "Gazelle"),
            correctIndex = 1,
            explanation = "A cheetah can sprint at speeds over 100 km/h in short bursts."
        ),
        QuizQuestion(
            id = "gen_55",
            category = QuestionCategory.GENERAL,
            questionText = "How many days are there in a leap year?",
            options = listOf("364", "365", "366", "367"),
            correctIndex = 2,
            explanation = "A leap year adds February 29, giving 366 days."
        ),
        QuizQuestion(
            id = "gen_56",
            category = QuestionCategory.GENERAL,
            questionText = "Which polygon has exactly 5 sides?",
            options = listOf("Hexagon", "Pentagon", "Octagon", "Triangle"),
            correctIndex = 1,
            explanation = "A pentagon has 5 straight sides and 5 angles."
        ),
        QuizQuestion(
            id = "gen_57",
            category = QuestionCategory.GENERAL,
            questionText = "What do bees collect from flowers to make honey?",
            options = listOf("Dew", "Nectar", "Sap", "Seeds"),
            correctIndex = 1,
            explanation = "Bees gather sweet nectar from blossoms and turn it into honey."
        ),
        QuizQuestion(
            id = "gen_58",
            category = QuestionCategory.GENERAL,
            questionText = "Which bird is famous for being the largest living bird and unable to fly?",
            options = listOf("Eagle", "Ostrich", "Peacock", "Albatross"),
            correctIndex = 1,
            explanation = "The ostrich is the tallest and heaviest living bird."
        ),
        QuizQuestion(
            id = "gen_59",
            category = QuestionCategory.SPORTS_ARTS,
            questionText = "How many strings does a standard acoustic guitar have?",
            options = listOf("4", "5", "6", "8"),
            correctIndex = 2,
            explanation = "A standard guitar has 6 strings tuned E-A-D-G-B-E."
        ),
        QuizQuestion(
            id = "gen_60",
            category = QuestionCategory.GENERAL,
            questionText = "Which direction does a magnetic compass needle always point toward?",
            options = listOf("East", "South", "North", "West"),
            correctIndex = 2,
            explanation = "Earth's magnetic field aligns a compass needle toward magnetic North."
        )
    )
}
