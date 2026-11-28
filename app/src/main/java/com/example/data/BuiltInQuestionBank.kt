package com.example.data

import com.example.R
import com.example.data.model.QuestionCategory
import com.example.data.model.QuizQuestion

object BuiltInQuestionBank {
    val questions: List<QuizQuestion> = listOf(
        // ==================== CURRENT AFFAIRS ====================
        QuizQuestion(
            id = "ca_1",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "Which space agency achieved the historic Chandrayaan-3 soft landing near the Moon's south pole?",
            options = listOf("NASA", "ISRO", "ESA", "JAXA"),
            correctIndex = 1,
            explanation = "India's ISRO successfully landed Chandrayaan-3 near the lunar south pole in August 2023.",
            minGrade = 1, maxGrade = 12,
            localImageRes = R.drawable.img_current_affairs_space
        ),
        QuizQuestion(
            id = "ca_2",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "Which clean energy sources shown in eco-cities convert sunlight and moving air directly into electricity?",
            options = listOf("Coal and oil", "Solar panels and wind turbines", "Diesel generators", "Natural gas boilers"),
            correctIndex = 1,
            explanation = "Solar photovoltaic panels and wind turbines generate renewable electricity without polluting smoke.",
            minGrade = 1, maxGrade = 10,
            localImageRes = R.drawable.img_current_affairs_nature
        ),
        QuizQuestion(
            id = "ca_3",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "What is the name of the NASA program aimed at returning astronauts to the Moon and preparing for Mars?",
            options = listOf("Voyager", "Artemis", "Hubble", "Pioneer"),
            correctIndex = 1,
            explanation = "The Artemis program leads modern human lunar exploration.",
            minGrade = 4, maxGrade = 12,
            localImageRes = R.drawable.img_current_affairs_space
        ),
        QuizQuestion(
            id = "ca_4",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "Why do modern green cities plant rooftop gardens and preserve river corridors?",
            options = listOf("To block sunlight", "To cool cities, clean air, and support wildlife", "To remove footpaths", "To reduce rain"),
            correctIndex = 1,
            explanation = "Urban greenery lowers heat, absorbs carbon dioxide, and creates habitats for birds and wildlife.",
            minGrade = 1, maxGrade = 8,
            localImageRes = R.drawable.img_current_affairs_nature
        ),
        QuizQuestion(
            id = "ca_5",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "Which powerful space telescope orbits far beyond the Moon to capture infrared images of early galaxies?",
            options = listOf("James Webb Space Telescope", "Galileo Spyglass", "Sputnik 1", "Cassini Probe"),
            correctIndex = 0,
            explanation = "The James Webb Space Telescope (JWST) observes distant galaxies in infrared light.",
            minGrade = 5, maxGrade = 12,
            localImageRes = R.drawable.img_current_affairs_space
        ),
        QuizQuestion(
            id = "ca_6",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "Which international agreement brings countries together to limit global warming and protect our climate?",
            options = listOf("Paris Agreement", "Magna Carta", "Olympic Charter", "Maritime Law"),
            correctIndex = 0,
            explanation = "The Paris Agreement unites nations to cut greenhouse gas emissions and adopt clean energy.",
            minGrade = 6, maxGrade = 12,
            localImageRes = R.drawable.img_current_affairs_nature
        ),
        QuizQuestion(
            id = "ca_7",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "Which Indian solar observatory mission studies the Sun from the L1 Lagrange point?",
            options = listOf("Aditya-L1", "Mangalyaan-2", "Gaganyaan", "Vikram-S"),
            correctIndex = 0,
            explanation = "Aditya-L1 is India's first dedicated space-based solar mission.",
            minGrade = 5, maxGrade = 12,
            localImageRes = R.drawable.img_current_affairs_space
        ),
        QuizQuestion(
            id = "ca_8",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "World Environment Day is celebrated around the globe every year on which date?",
            options = listOf("April 1", "June 5", "August 15", "December 25"),
            correctIndex = 1,
            explanation = "June 5 is observed worldwide as World Environment Day.",
            minGrade = 1, maxGrade = 12,
            localImageRes = R.drawable.img_current_affairs_nature
        ),
        QuizQuestion(
            id = "ca_9",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "Which city hosted the 2024 Summer Olympic Games?",
            options = listOf("Tokyo", "Paris", "Los Angeles", "London"),
            correctIndex = 1,
            explanation = "Paris, France hosted the 2024 Summer Olympics.",
            minGrade = 1, maxGrade = 12
        ),
        QuizQuestion(
            id = "ca_10",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "What does 'AI' stand for in modern computer science and robotics?",
            options = listOf("Automated Internet", "Artificial Intelligence", "Aerial Imaging", "Applied Index"),
            correctIndex = 1,
            explanation = "AI stands for Artificial Intelligence — software that solves problems and assists learning.",
            minGrade = 1, maxGrade = 12
        ),
        QuizQuestion(
            id = "ca_11",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "What is the name of India's upcoming human spaceflight mission to send astronauts into low Earth orbit?",
            options = listOf("Gaganyaan", "Chandrayaan-1", "NavIC", "Astrosat"),
            correctIndex = 0,
            explanation = "Gaganyaan is ISRO's crewed orbital spacecraft mission.",
            minGrade = 6, maxGrade = 12,
            localImageRes = R.drawable.img_current_affairs_space
        ),
        QuizQuestion(
            id = "ca_12",
            category = QuestionCategory.CURRENT_AFFAIRS,
            questionText = "Which international summit brings heads of 19 countries plus the European Union and African Union together on global economics?",
            options = listOf("G20", "ASEAN", "SAARC", "OPEC"),
            correctIndex = 0,
            explanation = "The G20 includes the world's major economies and welcomed the African Union as a permanent member.",
            minGrade = 8, maxGrade = 12
        ),

        // ==================== SCIENCE & SPACE ====================
        QuizQuestion(
            id = "sc_11",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "Which planet in our solar system is famous for its bright icy rings?",
            options = listOf("Mercury", "Saturn", "Mars", "Venus"),
            correctIndex = 1,
            explanation = "Saturn has the largest and brightest ring system made of ice and rock particles.",
            minGrade = 1, maxGrade = 10,
            localImageRes = R.drawable.img_current_affairs_space
        ),
        QuizQuestion(
            id = "sc_12",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "What gas do green plants absorb from the air to make food during photosynthesis?",
            options = listOf("Carbon dioxide", "Nitrogen", "Helium", "Argon"),
            correctIndex = 0,
            explanation = "Plants use sunlight, water, and carbon dioxide to produce glucose and release oxygen.",
            minGrade = 2, maxGrade = 12,
            localImageRes = R.drawable.img_current_affairs_nature
        ),
        QuizQuestion(
            id = "sc_13",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "How many planets are officially recognized in our solar system?",
            options = listOf("7", "8", "9", "10"),
            correctIndex = 1,
            explanation = "There are 8 planets: Mercury, Venus, Earth, Mars, Jupiter, Saturn, Uranus, and Neptune.",
            minGrade = 1, maxGrade = 7
        ),
        QuizQuestion(
            id = "sc_14",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "Which organ pumps blood to every part of the human body?",
            options = listOf("Liver", "Heart", "Kidney", "Stomach"),
            correctIndex = 1,
            explanation = "The heart is a muscular organ that circulates oxygen-rich blood throughout the body.",
            minGrade = 1, maxGrade = 8
        ),
        QuizQuestion(
            id = "sc_15",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "What is the boiling point of pure water at sea level?",
            options = listOf("50°C", "90°C", "100°C", "120°C"),
            correctIndex = 2,
            explanation = "At standard atmospheric pressure, pure water boils at 100 degrees Celsius.",
            minGrade = 2, maxGrade = 10
        ),
        QuizQuestion(
            id = "sc_16",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "Which is the largest planet in our solar system?",
            options = listOf("Earth", "Jupiter", "Neptune", "Mars"),
            correctIndex = 1,
            explanation = "Jupiter is a gas giant more than twice as massive as all other planets combined.",
            minGrade = 1, maxGrade = 8
        ),
        QuizQuestion(
            id = "sc_17",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "Which vitamin does human skin naturally make when exposed to gentle morning sunlight?",
            options = listOf("Vitamin A", "Vitamin C", "Vitamin D", "Vitamin K"),
            correctIndex = 2,
            explanation = "Sunlight triggers Vitamin D production, which keeps bones and teeth strong.",
            minGrade = 3, maxGrade = 10
        ),
        QuizQuestion(
            id = "sc_18",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "What force keeps the Moon in orbit around Earth and keeps our feet on the ground?",
            options = listOf("Magnetism", "Friction", "Gravity", "Electricity"),
            correctIndex = 2,
            explanation = "Gravity pulls objects with mass toward each other and holds planets and moons in orbit.",
            minGrade = 2, maxGrade = 12
        ),
        QuizQuestion(
            id = "sc_19",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "Which metal is liquid at room temperature and used in some classic thermometers?",
            options = listOf("Iron", "Mercury", "Copper", "Aluminium"),
            correctIndex = 1,
            explanation = "Mercury is the only metallic element that is liquid at standard room temperature.",
            minGrade = 5, maxGrade = 12
        ),
        QuizQuestion(
            id = "sc_20",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "What part of a flower produces pollen?",
            options = listOf("Root", "Anther", "Stem", "Petal"),
            correctIndex = 1,
            explanation = "The anther, part of the stamen, produces pollen grains.",
            minGrade = 4, maxGrade = 10
        ),
        QuizQuestion(
            id = "sc_21",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "Approximately how long does it take light from the Sun to reach Earth?",
            options = listOf("8 seconds", "8 minutes", "8 hours", "8 days"),
            correctIndex = 1,
            explanation = "Sunlight travels about 150 million km to reach Earth in roughly 8 minutes and 20 seconds.",
            minGrade = 5, maxGrade = 12
        ),
        QuizQuestion(
            id = "sc_22",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "What instrument is used to view tiny cells and microbes that are invisible to the naked eye?",
            options = listOf("Telescope", "Microscope", "Barometer", "Compass"),
            correctIndex = 1,
            explanation = "A microscope magnifies tiny objects such as plant cells and bacteria.",
            minGrade = 2, maxGrade = 9
        ),
        QuizQuestion(
            id = "sc_23",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "Which gas makes up about 78% of Earth's atmosphere?",
            options = listOf("Oxygen", "Carbon dioxide", "Nitrogen", "Hydrogen"),
            correctIndex = 2,
            explanation = "Earth's air is roughly 78% nitrogen, 21% oxygen, and 1% other gases.",
            minGrade = 5, maxGrade = 12
        ),
        QuizQuestion(
            id = "sc_24",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "What is the powerhouse organelle of a cell that produces energy (ATP)?",
            options = listOf("Ribosome", "Mitochondrion", "Golgi body", "Vacuole"),
            correctIndex = 1,
            explanation = "Mitochondria convert nutrients into usable cellular energy (ATP).",
            minGrade = 7, maxGrade = 12
        ),
        QuizQuestion(
            id = "sc_25",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "What is the chemical symbol for Sodium?",
            options = listOf("So", "Sd", "Na", "S"),
            correctIndex = 2,
            explanation = "Sodium's symbol Na comes from its Latin name 'natrium'.",
            minGrade = 7, maxGrade = 12
        ),
        QuizQuestion(
            id = "sc_26",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "According to Ohm's Law, voltage (V) equals current (I) multiplied by what?",
            options = listOf("Power", "Resistance", "Charge", "Capacitance"),
            correctIndex = 1,
            explanation = "Ohm's Law states V = I × R, where R is electrical resistance in ohms.",
            minGrade = 9, maxGrade = 12
        ),
        QuizQuestion(
            id = "sc_27",
            category = QuestionCategory.SCIENCE_SPACE,
            questionText = "Which subatomic particle carries a negative electric charge?",
            options = listOf("Proton", "Neutron", "Electron", "Positron"),
            correctIndex = 2,
            explanation = "Electrons carry a negative charge, protons carry positive, and neutrons are neutral.",
            minGrade = 7, maxGrade = 12
        ),

        // ==================== MATHEMATICS ====================
        QuizQuestion(
            id = "math_1",
            category = QuestionCategory.MATHEMATICS,
            questionText = "What is 7 × 8?",
            options = listOf("48", "54", "56", "64"),
            correctIndex = 2,
            explanation = "Seven groups of eight equal 56.",
            minGrade = 1, maxGrade = 5
        ),
        QuizQuestion(
            id = "math_2",
            category = QuestionCategory.MATHEMATICS,
            questionText = "How many minutes are in 2 hours and 15 minutes?",
            options = listOf("120", "135", "145", "150"),
            correctIndex = 1,
            explanation = "2 hours = 120 minutes, and 120 + 15 = 135 minutes.",
            minGrade = 1, maxGrade = 6
        ),
        QuizQuestion(
            id = "math_3",
            category = QuestionCategory.MATHEMATICS,
            questionText = "What is the perimeter of a square whose side length is 9 cm?",
            options = listOf("18 cm", "27 cm", "36 cm", "81 cm"),
            correctIndex = 2,
            explanation = "A square has 4 equal sides: 4 × 9 cm = 36 cm.",
            minGrade = 2, maxGrade = 7
        ),
        QuizQuestion(
            id = "math_4",
            category = QuestionCategory.MATHEMATICS,
            questionText = "Which fraction is equivalent to 50%?",
            options = listOf("1/4", "1/3", "1/2", "2/3"),
            correctIndex = 2,
            explanation = "50 out of 100 simplifies to 1/2.",
            minGrade = 3, maxGrade = 8
        ),
        QuizQuestion(
            id = "math_5",
            category = QuestionCategory.MATHEMATICS,
            questionText = "What is the sum of the three interior angles of any triangle?",
            options = listOf("90°", "180°", "270°", "360°"),
            correctIndex = 1,
            explanation = "The interior angles of every Euclidean triangle always add up to 180 degrees.",
            minGrade = 4, maxGrade = 12
        ),
        QuizQuestion(
            id = "math_6",
            category = QuestionCategory.MATHEMATICS,
            questionText = "What is the smallest prime number?",
            options = listOf("0", "1", "2", "3"),
            correctIndex = 2,
            explanation = "2 is the smallest prime number and the only even prime number.",
            minGrade = 4, maxGrade = 12
        ),
        QuizQuestion(
            id = "math_7",
            category = QuestionCategory.MATHEMATICS,
            questionText = "What is 15% of 200?",
            options = listOf("20", "25", "30", "35"),
            correctIndex = 2,
            explanation = "(15 / 100) × 200 = 30.",
            minGrade = 5, maxGrade = 12
        ),
        QuizQuestion(
            id = "math_8",
            category = QuestionCategory.MATHEMATICS,
            questionText = "If 3x + 7 = 22, what is the value of x?",
            options = listOf("3", "4", "5", "6"),
            correctIndex = 2,
            explanation = "Subtract 7 from both sides to get 3x = 15, then divide by 3 to get x = 5.",
            minGrade = 6, maxGrade = 12
        ),
        QuizQuestion(
            id = "math_9",
            category = QuestionCategory.MATHEMATICS,
            questionText = "What is the area of a right triangle with base 10 cm and height 6 cm?",
            options = listOf("16 cm²", "30 cm²", "45 cm²", "60 cm²"),
            correctIndex = 1,
            explanation = "Area of a triangle = 1/2 × base × height = 1/2 × 10 × 6 = 30 cm².",
            minGrade = 5, maxGrade = 12
        ),
        QuizQuestion(
            id = "math_10",
            category = QuestionCategory.MATHEMATICS,
            questionText = "In a right-angled triangle with legs 3 cm and 4 cm, what is the hypotenuse?",
            options = listOf("5 cm", "6 cm", "7 cm", "12 cm"),
            correctIndex = 0,
            explanation = "By the Pythagorean theorem, √(3² + 4²) = √(9 + 16) = √25 = 5 cm.",
            minGrade = 7, maxGrade = 12
        ),
        QuizQuestion(
            id = "math_11",
            category = QuestionCategory.MATHEMATICS,
            questionText = "What is the value of sin(90°) in trigonometry?",
            options = listOf("0", "0.5", "1", "√3 / 2"),
            correctIndex = 2,
            explanation = "In a right triangle / unit circle, sin(90°) equals 1.",
            minGrade = 9, maxGrade = 12
        ),
        QuizQuestion(
            id = "math_12",
            category = QuestionCategory.MATHEMATICS,
            questionText = "If you roll a fair 6-sided die, what is the probability of getting an even number?",
            options = listOf("1/6", "1/3", "1/2", "2/3"),
            correctIndex = 2,
            explanation = "Three out of six faces (2, 4, 6) are even, so 3/6 = 1/2.",
            minGrade = 6, maxGrade = 12
        ),

        // ==================== ENGLISH & BOOKS ====================
        QuizQuestion(
            id = "eng_1",
            category = QuestionCategory.ENGLISH_LITERATURE,
            questionText = "Which word is a noun in the sentence: 'The happy puppy ran fast'?",
            options = listOf("happy", "puppy", "ran", "fast"),
            correctIndex = 1,
            explanation = "'Puppy' names an animal, so it is a noun.",
            minGrade = 1, maxGrade = 5
        ),
        QuizQuestion(
            id = "eng_2",
            category = QuestionCategory.ENGLISH_LITERATURE,
            questionText = "What is the plural form of the word 'child'?",
            options = listOf("childs", "children", "childes", "childrens"),
            correctIndex = 1,
            explanation = "'Children' is the irregular plural of 'child'.",
            minGrade = 1, maxGrade = 6
        ),
        QuizQuestion(
            id = "eng_3",
            category = QuestionCategory.ENGLISH_LITERATURE,
            questionText = "Which word has the opposite meaning (antonym) of 'ancient'?",
            options = listOf("Old", "Modern", "Dusty", "Quiet"),
            correctIndex = 1,
            explanation = "Modern means new or present-day, the opposite of ancient.",
            minGrade = 3, maxGrade = 10
        ),
        QuizQuestion(
            id = "eng_4",
            category = QuestionCategory.ENGLISH_LITERATURE,
            questionText = "Who wrote the classic Indian collection of short stories 'Malgudi Days'?",
            options = listOf("R. K. Narayan", "Ruskin Bond", "Vikram Seth", "Arundhati Roy"),
            correctIndex = 0,
            explanation = "R. K. Narayan created the beloved fictional town of Malgudi.",
            minGrade = 4, maxGrade = 12
        ),
        QuizQuestion(
            id = "eng_5",
            category = QuestionCategory.ENGLISH_LITERATURE,
            questionText = "Who is the author of 'The Jungle Book', featuring Mowgli and Baloo?",
            options = listOf("Roald Dahl", "Rudyard Kipling", "Lewis Carroll", "Enid Blyton"),
            correctIndex = 1,
            explanation = "Rudyard Kipling published 'The Jungle Book' in 1894.",
            minGrade = 2, maxGrade = 9
        ),
        QuizQuestion(
            id = "eng_6",
            category = QuestionCategory.ENGLISH_LITERATURE,
            questionText = "Which figure of speech compares two things directly using 'like' or 'as'?",
            options = listOf("Simile", "Alliteration", "Hyperbole", "Onomatopoeia"),
            correctIndex = 0,
            explanation = "A simile compares using 'like' or 'as' (for example, 'brave as a lion').",
            minGrade = 5, maxGrade = 12
        ),
        QuizQuestion(
            id = "eng_7",
            category = QuestionCategory.ENGLISH_LITERATURE,
            questionText = "Which playwright wrote 'Romeo and Juliet' and 'Macbeth'?",
            options = listOf("Charles Dickens", "William Shakespeare", "Jane Austen", "Mark Twain"),
            correctIndex = 1,
            explanation = "William Shakespeare wrote both plays in the late 16th and early 17th centuries.",
            minGrade = 6, maxGrade = 12
        ),
        QuizQuestion(
            id = "eng_8",
            category = QuestionCategory.ENGLISH_LITERATURE,
            questionText = "In grammar, what part of speech describes a verb, adjective, or another adverb?",
            options = listOf("Pronoun", "Conjunction", "Adverb", "Preposition"),
            correctIndex = 2,
            explanation = "An adverb modifies a verb, adjective, or another adverb (for example, 'quickly').",
            minGrade = 4, maxGrade = 11
        ),
        QuizQuestion(
            id = "eng_9",
            category = QuestionCategory.ENGLISH_LITERATURE,
            questionText = "What does the idiom 'hit the books' mean?",
            options = listOf("Close a library", "Begin studying seriously", "Drop a notebook", "Buy new novels"),
            correctIndex = 1,
            explanation = "'Hit the books' means to study hard for school or an upcoming exam.",
            minGrade = 5, maxGrade = 12
        ),
        QuizQuestion(
            id = "eng_10",
            category = QuestionCategory.ENGLISH_LITERATURE,
            questionText = "Who wrote the autobiography 'Wings of Fire'?",
            options = listOf("Dr. A. P. J. Abdul Kalam", "Rabindranath Tagore", "Mahatma Gandhi", "Jawaharlal Nehru"),
            correctIndex = 0,
            explanation = "'Wings of Fire' is the inspiring autobiography of Dr. A. P. J. Abdul Kalam.",
            minGrade = 6, maxGrade = 12
        ),

        // ==================== GEOGRAPHY ====================
        QuizQuestion(
            id = "geo_25",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which is the largest ocean on Earth?",
            options = listOf("Atlantic Ocean", "Indian Ocean", "Pacific Ocean", "Arctic Ocean"),
            correctIndex = 2,
            explanation = "The Pacific Ocean covers about one-third of Earth's surface.",
            minGrade = 1, maxGrade = 10
        ),
        QuizQuestion(
            id = "geo_26",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which continent is home to the Sahara, the world's largest hot desert?",
            options = listOf("Asia", "Africa", "Australia", "South America"),
            correctIndex = 1,
            explanation = "The Sahara Desert spans much of northern Africa.",
            minGrade = 2, maxGrade = 10
        ),
        QuizQuestion(
            id = "geo_27",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "What is the capital city of Japan?",
            options = listOf("Kyoto", "Osaka", "Tokyo", "Seoul"),
            correctIndex = 2,
            explanation = "Tokyo has been Japan's capital since 1868.",
            minGrade = 3, maxGrade = 12
        ),
        QuizQuestion(
            id = "geo_28",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which mountain peak is the highest point above sea level on Earth?",
            options = listOf("K2", "Mount Everest", "Kangchenjunga", "Mount Kilimanjaro"),
            correctIndex = 1,
            explanation = "Mount Everest in the Himalayas rises 8,848.86 meters above sea level.",
            minGrade = 1, maxGrade = 12
        ),
        QuizQuestion(
            id = "geo_29",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which river flows through Egypt and is among the longest rivers in the world?",
            options = listOf("Amazon", "Nile", "Ganga", "Danube"),
            correctIndex = 1,
            explanation = "The Nile River flows northward through northeastern Africa into the Mediterranean Sea.",
            minGrade = 3, maxGrade = 12
        ),
        QuizQuestion(
            id = "geo_30",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which country is both a country and a continent?",
            options = listOf("Brazil", "Canada", "Australia", "India"),
            correctIndex = 2,
            explanation = "Australia is the world's smallest continent and a single nation.",
            minGrade = 1, maxGrade = 9
        ),
        QuizQuestion(
            id = "geo_31",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "What is the capital city of India?",
            options = listOf("Mumbai", "New Delhi", "Bengaluru", "Kolkata"),
            correctIndex = 1,
            explanation = "New Delhi is the capital city of India.",
            minGrade = 1, maxGrade = 8
        ),
        QuizQuestion(
            id = "geo_32",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which imaginary line divides Earth into the Northern and Southern Hemispheres?",
            options = listOf("Prime Meridian", "Tropic of Cancer", "Equator", "Arctic Circle"),
            correctIndex = 2,
            explanation = "The Equator sits at 0 degrees latitude around Earth's middle.",
            minGrade = 3, maxGrade = 12
        ),
        QuizQuestion(
            id = "geo_33",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which rainforest is often called the 'lungs of the Earth'?",
            options = listOf("Congo Rainforest", "Amazon Rainforest", "Daintree Rainforest", "Sundarbans"),
            correctIndex = 1,
            explanation = "The Amazon Rainforest in South America is the largest tropical rainforest on Earth.",
            minGrade = 2, maxGrade = 10,
            localImageRes = R.drawable.img_current_affairs_nature
        ),
        QuizQuestion(
            id = "geo_34",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which country is known as the 'Land of the Rising Sun'?",
            options = listOf("Thailand", "Japan", "Norway", "New Zealand"),
            correctIndex = 1,
            explanation = "Japan's name (Nihon/Nippon) literally means 'origin of the sun'.",
            minGrade = 2, maxGrade = 10
        ),
        QuizQuestion(
            id = "geo_35",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which continent has no permanent cities and is covered mostly in ice?",
            options = listOf("Europe", "Antarctica", "South America", "Africa"),
            correctIndex = 1,
            explanation = "Antarctica hosts scientific research stations rather than permanent cities.",
            minGrade = 1, maxGrade = 9
        ),
        QuizQuestion(
            id = "geo_36",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "How many continents are there on Earth?",
            options = listOf("5", "6", "7", "8"),
            correctIndex = 2,
            explanation = "Asia, Africa, North America, South America, Antarctica, Europe, and Australia make 7.",
            minGrade = 1, maxGrade = 7
        ),
        QuizQuestion(
            id = "geo_37",
            category = QuestionCategory.GEOGRAPHY,
            questionText = "Which imaginary vertical line at 0° longitude passes through Greenwich, London?",
            options = listOf("International Date Line", "Prime Meridian", "Tropic of Capricorn", "Equator"),
            correctIndex = 1,
            explanation = "The Prime Meridian is at 0 degrees longitude and serves as the reference for GMT/UTC time.",
            minGrade = 6, maxGrade = 12
        ),

        // ==================== HISTORY & CIVICS ====================
        QuizQuestion(
            id = "his_37",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Who was the first Prime Minister of independent India?",
            options = listOf("Sardar Vallabhbhai Patel", "Jawaharlal Nehru", "Dr. Rajendra Prasad", "Subhas Chandra Bose"),
            correctIndex = 1,
            explanation = "Pandit Jawaharlal Nehru served as India's first Prime Minister from 1947.",
            minGrade = 2, maxGrade = 12
        ),
        QuizQuestion(
            id = "his_38",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Who was the first human to step onto the surface of the Moon in 1969?",
            options = listOf("Buzz Aldrin", "Yuri Gagarin", "Neil Armstrong", "Kalpana Chawla"),
            correctIndex = 2,
            explanation = "Neil Armstrong stepped onto the Moon during Apollo 11 on July 20, 1969.",
            minGrade = 1, maxGrade = 10
        ),
        QuizQuestion(
            id = "his_39",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Which ancient civilization built the Great Pyramids of Giza?",
            options = listOf("Roman", "Egyptian", "Mesopotamian", "Mayan"),
            correctIndex = 1,
            explanation = "Ancient Egyptians built the Great Pyramids near Cairo over 4,500 years ago.",
            minGrade = 2, maxGrade = 12
        ),
        QuizQuestion(
            id = "his_40",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Who is famously known as the 'Father of the Indian Constitution'?",
            options = listOf("Mahatma Gandhi", "Dr. B. R. Ambedkar", "Dr. A. P. J. Abdul Kalam", "Rabindranath Tagore"),
            correctIndex = 1,
            explanation = "Dr. Bhimrao Ramji Ambedkar chaired the Drafting Committee of the Indian Constitution.",
            minGrade = 4, maxGrade = 12
        ),
        QuizQuestion(
            id = "his_41",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Who invented the practical incandescent light bulb and phonograph?",
            options = listOf("Thomas Edison", "Isaac Newton", "Albert Einstein", "Galileo Galilei"),
            correctIndex = 0,
            explanation = "Thomas Edison developed the first commercially practical incandescent light bulb.",
            minGrade = 2, maxGrade = 9
        ),
        QuizQuestion(
            id = "his_42",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Which renowned scientist discovered the laws of motion and universal gravitation?",
            options = listOf("Charles Darwin", "Sir Isaac Newton", "Marie Curie", "Louis Pasteur"),
            correctIndex = 1,
            explanation = "Sir Isaac Newton formulated the three laws of motion and gravity.",
            minGrade = 4, maxGrade = 12
        ),
        QuizQuestion(
            id = "his_43",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "In which year did the United Nations (UN) officially come into existence?",
            options = listOf("1919", "1945", "1955", "1969"),
            correctIndex = 1,
            explanation = "The United Nations was founded on October 24, 1945.",
            minGrade = 6, maxGrade = 12
        ),
        QuizQuestion(
            id = "his_44",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Who wrote India's National Anthem 'Jana Gana Mana'?",
            options = listOf("Bankim Chandra Chatterjee", "Rabindranath Tagore", "Sarojini Naidu", "Premchand"),
            correctIndex = 1,
            explanation = "Nobel Laureate Rabindranath Tagore composed 'Jana Gana Mana'.",
            minGrade = 1, maxGrade = 10
        ),
        QuizQuestion(
            id = "his_45",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Who was the first woman to win a Nobel Prize?",
            options = listOf("Rosalind Franklin", "Marie Curie", "Florence Nightingale", "Ada Lovelace"),
            correctIndex = 1,
            explanation = "Marie Curie won the Nobel Prize in Physics in 1903 and Chemistry in 1911.",
            minGrade = 5, maxGrade = 12
        ),
        QuizQuestion(
            id = "his_46",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Which famous brothers achieved the first powered airplane flight in 1903?",
            options = listOf("Lumiere Brothers", "Wright Brothers", "Grimm Brothers", "Montgolfier Brothers"),
            correctIndex = 1,
            explanation = "Orville and Wilbur Wright flew the first powered aircraft at Kitty Hawk in 1903.",
            minGrade = 2, maxGrade = 10
        ),
        QuizQuestion(
            id = "his_47",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "What is the supreme law-making document of a democratic country called?",
            options = listOf("Almanac", "Constitution", "Dictionary", "Atlas"),
            correctIndex = 1,
            explanation = "A Constitution defines citizens' rights, duties, and how government works.",
            minGrade = 4, maxGrade = 12
        ),
        QuizQuestion(
            id = "his_48",
            category = QuestionCategory.HISTORY_CIVICS,
            questionText = "Who is celebrated as the 'Missile Man of India' and served as India's 11th President?",
            options = listOf("Dr. C. V. Raman", "Dr. A. P. J. Abdul Kalam", "Vikram Sarabhai", "Homi Bhabha"),
            correctIndex = 1,
            explanation = "Dr. A. P. J. Abdul Kalam inspired millions of students through science and aerospace leadership.",
            minGrade = 3, maxGrade = 12
        ),

        // ==================== COMPUTER & TECH ====================
        QuizQuestion(
            id = "tech_1",
            category = QuestionCategory.COMPUTER_TECH,
            questionText = "Which device is used to move the pointer and click items on a computer screen?",
            options = listOf("Printer", "Mouse", "Speaker", "Scanner"),
            correctIndex = 1,
            explanation = "A mouse controls the cursor on screen.",
            minGrade = 1, maxGrade = 5
        ),
        QuizQuestion(
            id = "tech_2",
            category = QuestionCategory.COMPUTER_TECH,
            questionText = "What does 'CPU' stand for in a computer?",
            options = listOf("Central Processing Unit", "Computer Power Unit", "Core Printing Utility", "Central Program User"),
            correctIndex = 0,
            explanation = "The CPU (Central Processing Unit) executes instructions like the brain of a computer.",
            minGrade = 2, maxGrade = 10
        ),
        QuizQuestion(
            id = "tech_3",
            category = QuestionCategory.COMPUTER_TECH,
            questionText = "How many bits make 1 byte?",
            options = listOf("2", "4", "8", "16"),
            correctIndex = 2,
            explanation = "One byte consists of 8 binary bits.",
            minGrade = 4, maxGrade = 12
        ),
        QuizQuestion(
            id = "tech_4",
            category = QuestionCategory.COMPUTER_TECH,
            questionText = "Which two digits are used in the binary number system?",
            options = listOf("0 and 1", "1 and 2", "0 and 9", "2 and 8"),
            correctIndex = 0,
            explanation = "Computers store and process data in binary using only 0s and 1s.",
            minGrade = 4, maxGrade = 12
        ),
        QuizQuestion(
            id = "tech_5",
            category = QuestionCategory.COMPUTER_TECH,
            questionText = "What does 'WWW' stand for in a website address?",
            options = listOf("World Wide Web", "World Web Window", "Wide Wireless Wave", "Web Work World"),
            correctIndex = 0,
            explanation = "Tim Berners-Lee invented the World Wide Web (WWW).",
            minGrade = 2, maxGrade = 10
        ),
        QuizQuestion(
            id = "tech_6",
            category = QuestionCategory.COMPUTER_TECH,
            questionText = "What does 'RAM' stand for in computer memory?",
            options = listOf("Read All Memory", "Random Access Memory", "Rapid Active Module", "Run App Memory"),
            correctIndex = 1,
            explanation = "RAM stands for Random Access Memory, which holds active apps and data temporarily.",
            minGrade = 5, maxGrade = 12
        ),
        QuizQuestion(
            id = "tech_7",
            category = QuestionCategory.COMPUTER_TECH,
            questionText = "What does 'HTTP' stand for when your browser loads a web page?",
            options = listOf(
                "HyperText Transfer Protocol",
                "High Tech Text Program",
                "Hyper Tool Transmission Path",
                "Home Terminal Transfer Port"
            ),
            correctIndex = 0,
            explanation = "HTTP (HyperText Transfer Protocol) transfers web pages across the internet.",
            minGrade = 6, maxGrade = 12
        ),
        QuizQuestion(
            id = "tech_8",
            category = QuestionCategory.COMPUTER_TECH,
            questionText = "In programming, what is a step-by-step set of rules to solve a problem called?",
            options = listOf("Algorithm", "Pixel", "Modem", "Firewall"),
            correctIndex = 0,
            explanation = "An algorithm is a clear sequence of instructions to accomplish a task.",
            minGrade = 5, maxGrade = 12
        ),
        QuizQuestion(
            id = "tech_9",
            category = QuestionCategory.COMPUTER_TECH,
            questionText = "Which data structure follows the LIFO (Last In, First Out) principle?",
            options = listOf("Queue", "Stack", "Linked List", "Hash Table"),
            correctIndex = 1,
            explanation = "A stack pushes and pops elements from the top (Last In, First Out).",
            minGrade = 9, maxGrade = 12
        ),
        QuizQuestion(
            id = "tech_10",
            category = QuestionCategory.COMPUTER_TECH,
            questionText = "Which key combination on most keyboards copies selected text?",
            options = listOf("Ctrl + P", "Ctrl + C", "Ctrl + X", "Ctrl + Z"),
            correctIndex = 1,
            explanation = "Ctrl + C copies the selected text or item to the clipboard.",
            minGrade = 2, maxGrade = 10
        ),

        // ==================== ENVIRONMENT & NATURE ====================
        QuizQuestion(
            id = "env_1",
            category = QuestionCategory.ENVIRONMENT_NATURE,
            questionText = "What do we call the process of turning used paper, glass, and plastic into new products?",
            options = listOf("Littering", "Recycling", "Evaporation", "Mining"),
            correctIndex = 1,
            explanation = "Recycling reduces waste and saves natural resources and energy.",
            minGrade = 1, maxGrade = 8,
            localImageRes = R.drawable.img_current_affairs_nature
        ),
        QuizQuestion(
            id = "env_2",
            category = QuestionCategory.ENVIRONMENT_NATURE,
            questionText = "Which natural process turns liquid water from oceans and rivers into water vapor in the sky?",
            options = listOf("Condensation", "Evaporation", "Freezing", "Erosion"),
            correctIndex = 1,
            explanation = "Heat from the Sun causes evaporation, rising water vapor into the atmosphere.",
            minGrade = 2, maxGrade = 9
        ),
        QuizQuestion(
            id = "env_3",
            category = QuestionCategory.ENVIRONMENT_NATURE,
            questionText = "What layer high in Earth's stratosphere protects living things from harmful UV rays?",
            options = listOf("Ozone layer", "Cloud layer", "Crust layer", "Mantle"),
            correctIndex = 0,
            explanation = "The ozone layer absorbs most of the Sun's harmful ultraviolet radiation.",
            minGrade = 4, maxGrade = 12
        ),
        QuizQuestion(
            id = "env_4",
            category = QuestionCategory.ENVIRONMENT_NATURE,
            questionText = "Which type of animal eats only plants?",
            options = listOf("Carnivore", "Herbivore", "Scavenger", "Predator"),
            correctIndex = 1,
            explanation = "Herbivores like deer, cows, and elephants feed on plants.",
            minGrade = 1, maxGrade = 6
        ),
        QuizQuestion(
            id = "env_5",
            category = QuestionCategory.ENVIRONMENT_NATURE,
            questionText = "What are the '3 Rs' of caring for our environment?",
            options = listOf(
                "Run, Rest, Read",
                "Reduce, Reuse, Recycle",
                "Rain, River, Rock",
                "Repair, Race, Remove"
            ),
            correctIndex = 1,
            explanation = "Reduce, Reuse, and Recycle help minimize waste on Earth.",
            minGrade = 1, maxGrade = 8
        ),
        QuizQuestion(
            id = "env_6",
            category = QuestionCategory.ENVIRONMENT_NATURE,
            questionText = "Which coastal mangrove forest spanning India and Bangladesh is the largest in the world?",
            options = listOf("Sundarbans", "Amazon", "Taiga", "Savanna"),
            correctIndex = 0,
            explanation = "The Sundarbans delta is the world's largest mangrove forest and home to the Royal Bengal Tiger.",
            minGrade = 5, maxGrade = 12
        ),
        QuizQuestion(
            id = "env_7",
            category = QuestionCategory.ENVIRONMENT_NATURE,
            questionText = "Which gas is the primary driver of the modern human-caused greenhouse effect?",
            options = listOf("Helium", "Carbon dioxide", "Argon", "Neon"),
            correctIndex = 1,
            explanation = "Excess carbon dioxide from burning fossil fuels traps extra heat in the atmosphere.",
            minGrade = 6, maxGrade = 12
        ),
        QuizQuestion(
            id = "env_8",
            category = QuestionCategory.ENVIRONMENT_NATURE,
            questionText = "What do bees collect from flowers to make honey while helping plants pollinate?",
            options = listOf("Dew", "Nectar", "Sap", "Seeds"),
            correctIndex = 1,
            explanation = "Bees gather sweet nectar from blossoms and transfer pollen between flowers.",
            minGrade = 1, maxGrade = 7
        ),

        // ==================== SPORTS & ARTS ====================
        QuizQuestion(
            id = "gen_49",
            category = QuestionCategory.SPORTS_ARTS,
            questionText = "How many players are on a standard cricket team on the field at one time?",
            options = listOf("9", "10", "11", "12"),
            correctIndex = 2,
            explanation = "Each cricket side fields 11 players.",
            minGrade = 1, maxGrade = 12
        ),
        QuizQuestion(
            id = "gen_50",
            category = QuestionCategory.SPORTS_ARTS,
            questionText = "How many interlocking rings appear on the Olympic flag?",
            options = listOf("4", "5", "6", "7"),
            correctIndex = 1,
            explanation = "The 5 Olympic rings represent the union of five inhabited continents.",
            minGrade = 1, maxGrade = 12
        ),
        QuizQuestion(
            id = "gen_51",
            category = QuestionCategory.SPORTS_ARTS,
            questionText = "Who painted the famous artwork 'Mona Lisa'?",
            options = listOf("Pablo Picasso", "Leonardo da Vinci", "Vincent van Gogh", "Michelangelo"),
            correctIndex = 1,
            explanation = "Italian Renaissance artist Leonardo da Vinci painted the Mona Lisa.",
            minGrade = 3, maxGrade = 12
        ),
        QuizQuestion(
            id = "gen_52",
            category = QuestionCategory.SPORTS_ARTS,
            questionText = "Which board game uses pieces named King, Queen, Rook, Bishop, Knight, and Pawn?",
            options = listOf("Checkers", "Chess", "Ludo", "Carrom"),
            correctIndex = 1,
            explanation = "Chess is played on an 8x8 checkered board with these six piece types.",
            minGrade = 1, maxGrade = 12
        ),
        QuizQuestion(
            id = "gen_59",
            category = QuestionCategory.SPORTS_ARTS,
            questionText = "How many strings does a standard acoustic guitar have?",
            options = listOf("4", "5", "6", "8"),
            correctIndex = 2,
            explanation = "A standard guitar has 6 strings tuned E-A-D-G-B-E.",
            minGrade = 2, maxGrade = 12
        ),
        QuizQuestion(
            id = "sa_6",
            category = QuestionCategory.SPORTS_ARTS,
            questionText = "Which classical Indian dance form originated in the state of Odisha?",
            options = listOf("Kathakali", "Odissi", "Bharatanatyam", "Manipuri"),
            correctIndex = 1,
            explanation = "Odissi is an ancient classical dance form from Odisha.",
            minGrade = 4, maxGrade = 12
        ),
        QuizQuestion(
            id = "sa_7",
            category = QuestionCategory.SPORTS_ARTS,
            questionText = "In football (soccer), how many minutes long is a standard regulation match (excluding extra time)?",
            options = listOf("60 minutes", "75 minutes", "90 minutes", "120 minutes"),
            correctIndex = 2,
            explanation = "A standard football match has two halves of 45 minutes each, totaling 90 minutes.",
            minGrade = 2, maxGrade = 12
        ),
        QuizQuestion(
            id = "sa_8",
            category = QuestionCategory.SPORTS_ARTS,
            questionText = "What are the three primary colors in traditional painting (RYB model)?",
            options = listOf("Red, Yellow, Blue", "Green, Orange, Purple", "Black, White, Grey", "Pink, Brown, Cyan"),
            correctIndex = 0,
            explanation = "Red, yellow, and blue can be mixed to create secondary colors in painting.",
            minGrade = 1, maxGrade = 7
        ),

        // ==================== LOGICAL REASONING ====================
        QuizQuestion(
            id = "log_1",
            category = QuestionCategory.LOGICAL_REASONING,
            questionText = "Look at the number pattern: 2, 4, 6, 8, __. What number comes next?",
            options = listOf("9", "10", "11", "12"),
            correctIndex = 1,
            explanation = "Each number increases by 2, so 8 + 2 = 10.",
            minGrade = 1, maxGrade = 5
        ),
        QuizQuestion(
            id = "log_2",
            category = QuestionCategory.LOGICAL_REASONING,
            questionText = "Which item is the odd one out: Apple, Banana, Carrot, Mango?",
            options = listOf("Apple", "Banana", "Carrot", "Mango"),
            correctIndex = 2,
            explanation = "Carrot is a root vegetable, while the other three are fruits.",
            minGrade = 1, maxGrade = 6
        ),
        QuizQuestion(
            id = "log_3",
            category = QuestionCategory.LOGICAL_REASONING,
            questionText = "Find the next number in the sequence: 3, 6, 12, 24, __",
            options = listOf("30", "36", "42", "48"),
            correctIndex = 3,
            explanation = "Each number doubles: 24 × 2 = 48.",
            minGrade = 3, maxGrade = 10
        ),
        QuizQuestion(
            id = "log_4",
            category = QuestionCategory.LOGICAL_REASONING,
            questionText = "Doctor is to Hospital as Teacher is to ____.",
            options = listOf("Factory", "School", "Airport", "Stadium"),
            correctIndex = 1,
            explanation = "A doctor works in a hospital just as a teacher works in a school.",
            minGrade = 1, maxGrade = 8
        ),
        QuizQuestion(
            id = "log_5",
            category = QuestionCategory.LOGICAL_REASONING,
            questionText = "Look at the series: 1, 4, 9, 16, 25, __. What comes next?",
            options = listOf("30", "32", "36", "40"),
            correctIndex = 2,
            explanation = "These are square numbers (1², 2², 3², 4², 5²), so 6² = 36.",
            minGrade = 5, maxGrade = 12
        ),
        QuizQuestion(
            id = "log_6",
            category = QuestionCategory.LOGICAL_REASONING,
            questionText = "If CAT is coded as DBU (each letter shifted +1), how is DOG coded?",
            options = listOf("EPH", "DPI", "CNF", "FOH"),
            correctIndex = 0,
            explanation = "D -> E, O -> P, G -> H gives EPH.",
            minGrade = 4, maxGrade = 12
        ),
        QuizQuestion(
            id = "log_7",
            category = QuestionCategory.LOGICAL_REASONING,
            questionText = "A clock shows 3:00. What is the angle between the hour hand and minute hand?",
            options = listOf("45°", "60°", "90°", "120°"),
            correctIndex = 2,
            explanation = "At 3:00, the hands point at 12 and 3, which is one-quarter of 360° = 90°.",
            minGrade = 4, maxGrade = 12
        ),
        QuizQuestion(
            id = "log_8",
            category = QuestionCategory.LOGICAL_REASONING,
            questionText = "All roses are flowers. Some flowers fade quickly. Which statement MUST be true?",
            options = listOf(
                "All roses fade quickly",
                "Some roses are flowers",
                "No flowers are roses",
                "All flowers are roses"
            ),
            correctIndex = 1,
            explanation = "Since all roses are flowers, it follows that roses belong to the set of flowers.",
            minGrade = 7, maxGrade = 12
        ),

        // ==================== GENERAL KNOWLEDGE ====================
        QuizQuestion(
            id = "gen_53",
            category = QuestionCategory.GENERAL,
            questionText = "How many colors are visible in a classic rainbow?",
            options = listOf("5", "6", "7", "8"),
            correctIndex = 2,
            explanation = "VIBGYOR: Violet, Indigo, Blue, Green, Yellow, Orange, and Red.",
            minGrade = 1, maxGrade = 8
        ),
        QuizQuestion(
            id = "gen_54",
            category = QuestionCategory.GENERAL,
            questionText = "Which is the fastest land animal in the world?",
            options = listOf("Lion", "Cheetah", "Leopard", "Gazelle"),
            correctIndex = 1,
            explanation = "A cheetah can sprint at speeds over 100 km/h in short bursts.",
            minGrade = 1, maxGrade = 9
        ),
        QuizQuestion(
            id = "gen_55",
            category = QuestionCategory.GENERAL,
            questionText = "How many days are there in a leap year?",
            options = listOf("364", "365", "366", "367"),
            correctIndex = 2,
            explanation = "A leap year adds February 29, giving 366 days.",
            minGrade = 1, maxGrade = 10
        ),
        QuizQuestion(
            id = "gen_56",
            category = QuestionCategory.GENERAL,
            questionText = "Which polygon has exactly 5 sides?",
            options = listOf("Hexagon", "Pentagon", "Octagon", "Triangle"),
            correctIndex = 1,
            explanation = "A pentagon has 5 straight sides and 5 angles.",
            minGrade = 2, maxGrade = 9
        ),
        QuizQuestion(
            id = "gen_57",
            category = QuestionCategory.GENERAL,
            questionText = "Which animal is the largest mammal on Earth?",
            options = listOf("African Elephant", "Blue Whale", "Giraffe", "Polar Bear"),
            correctIndex = 1,
            explanation = "The Blue Whale is the largest animal known to have ever lived on Earth.",
            minGrade = 1, maxGrade = 9
        ),
        QuizQuestion(
            id = "gen_58",
            category = QuestionCategory.GENERAL,
            questionText = "Which bird is famous for being the largest living bird and unable to fly?",
            options = listOf("Eagle", "Ostrich", "Peacock", "Albatross"),
            correctIndex = 1,
            explanation = "The ostrich is the tallest and heaviest living bird.",
            minGrade = 1, maxGrade = 9
        ),
        QuizQuestion(
            id = "gen_60",
            category = QuestionCategory.GENERAL,
            questionText = "Which direction does a magnetic compass needle always point toward?",
            options = listOf("East", "South", "North", "West"),
            correctIndex = 2,
            explanation = "Earth's magnetic field aligns a compass needle toward magnetic North.",
            minGrade = 1, maxGrade = 10
        ),
        QuizQuestion(
            id = "gen_61",
            category = QuestionCategory.GENERAL,
            questionText = "Which organ helps humans breathe by taking in oxygen from the air?",
            options = listOf("Lungs", "Ears", "Teeth", "Nails"),
            correctIndex = 0,
            explanation = "The lungs exchange oxygen and carbon dioxide every time we breathe.",
            minGrade = 1, maxGrade = 6
        ),
        QuizQuestion(
            id = "gen_62",
            category = QuestionCategory.GENERAL,
            questionText = "How many months in a calendar year have 31 days?",
            options = listOf("5", "6", "7", "8"),
            correctIndex = 2,
            explanation = "Jan, Mar, May, Jul, Aug, Oct, and Dec have 31 days (7 months).",
            minGrade = 2, maxGrade = 10
        ),
        QuizQuestion(
            id = "gen_63",
            category = QuestionCategory.GENERAL,
            questionText = "Which currency is used in Japan?",
            options = listOf("Yuan", "Yen", "Won", "Euro"),
            correctIndex = 1,
            explanation = "The Japanese Yen (JPY) is the official currency of Japan.",
            minGrade = 5, maxGrade = 12
        ),
        QuizQuestion(
            id = "gen_64",
            category = QuestionCategory.GENERAL,
            questionText = "Which famous prize was established in 1901 in fields like Physics, Chemistry, Literature, and Peace?",
            options = listOf("Pulitzer Prize", "Nobel Prize", "Fields Medal", "Booker Prize"),
            correctIndex = 1,
            explanation = "The Nobel Prizes were created by Alfred Nobel's will and first awarded in 1901.",
            minGrade = 6, maxGrade = 12
        ),
        QuizQuestion(
            id = "gen_65",
            category = QuestionCategory.GENERAL,
            questionText = "What does 'UNESCO' work globally to protect and promote?",
            options = listOf(
                "Education, Science, and Culture",
                "Space Rockets Only",
                "Automobile Racing",
                "Private Banking"
            ),
            correctIndex = 0,
            explanation = "UNESCO stands for the United Nations Educational, Scientific and Cultural Organization.",
            minGrade = 6, maxGrade = 12
        ),
        QuizQuestion(
            id = "gen_66",
            category = QuestionCategory.GENERAL,
            questionText = "Which vitamin deficiency commonly leads to scurvy and bleeding gums?",
            options = listOf("Vitamin A", "Vitamin B12", "Vitamin C", "Vitamin E"),
            correctIndex = 2,
            explanation = "Vitamin C (found in citrus fruits) prevents scurvy and supports tissue healing.",
            minGrade = 5, maxGrade = 12
        ),
        QuizQuestion(
            id = "gen_67",
            category = QuestionCategory.GENERAL,
            questionText = "Which planet is closest in size to Earth and often called Earth's twin?",
            options = listOf("Mercury", "Venus", "Mars", "Neptune"),
            correctIndex = 1,
            explanation = "Venus has nearly the same diameter and mass as Earth.",
            minGrade = 4, maxGrade = 12
        ),
        QuizQuestion(
            id = "gen_68",
            category = QuestionCategory.GENERAL,
            questionText = "Which Indian classical language is one of the oldest surviving classical languages in southern India?",
            options = listOf("Tamil", "Esperanto", "Latin", "Swahili"),
            correctIndex = 0,
            explanation = "Tamil has over two thousand years of continuous classical literary heritage.",
            minGrade = 6, maxGrade = 12
        ),
        QuizQuestion(
            id = "gen_69",
            category = QuestionCategory.GENERAL,
            questionText = "What is the hardest naturally occurring mineral on Earth?",
            options = listOf("Quartz", "Gold", "Diamond", "Granite"),
            correctIndex = 2,
            explanation = "Diamond scores 10 out of 10 on the Mohs hardness scale.",
            minGrade = 3, maxGrade = 12
        ),
        QuizQuestion(
            id = "gen_70",
            category = QuestionCategory.GENERAL,
            questionText = "In the human body, which blood vessels carry blood away from the heart?",
            options = listOf("Veins", "Arteries", "Nerves", "Tendons"),
            correctIndex = 1,
            explanation = "Arteries carry blood away from the heart, while veins return blood to the heart.",
            minGrade = 6, maxGrade = 12
        ),
        QuizQuestion(
            id = "gen_71",
            category = QuestionCategory.GENERAL,
            questionText = "What is the study of stars, planets, and space called?",
            options = listOf("Botany", "Astronomy", "Geology", "Anatomy"),
            correctIndex = 1,
            explanation = "Astronomy is the scientific study of celestial objects and the universe.",
            minGrade = 3, maxGrade = 12
        ),
        QuizQuestion(
            id = "gen_72",
            category = QuestionCategory.GENERAL,
            questionText = "Which continent is the largest by both land area and population?",
            options = listOf("Africa", "North America", "Asia", "Europe"),
            correctIndex = 2,
            explanation = "Asia covers nearly 30% of Earth's land area and holds over half the world's people.",
            minGrade = 2, maxGrade = 12
        )
    )
}
