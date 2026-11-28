package com.example.util

data class LanguageOption(
    val code: String,
    val englishName: String,
    val nativeLabel: String
)

object LanguageCatalog {
    val languages = listOf(
        LanguageOption("en", "English", "English"),
        LanguageOption("hi", "Hindi", "हिन्दी"),
        LanguageOption("or", "Odia", "ଓଡ଼ିଆ"),
        LanguageOption("bn", "Bengali", "বাংলা"),
        LanguageOption("ta", "Tamil", "தமிழ்"),
        LanguageOption("te", "Telugu", "తెలుగు"),
        LanguageOption("mr", "Marathi", "मराठी"),
        LanguageOption("es", "Spanish", "Español"),
        LanguageOption("fr", "French", "Français")
    )

    fun normalize(raw: String?): String {
        if (raw.isNullOrBlank()) return "English"
        return languages.firstOrNull {
            it.englishName.equals(raw.trim(), ignoreCase = true) ||
                it.nativeLabel.equals(raw.trim(), ignoreCase = true)
        }?.englishName ?: "English"
    }

    fun displayLabel(language: String): String {
        val norm = normalize(language)
        val opt = languages.firstOrNull { it.englishName == norm } ?: return norm
        return if (opt.nativeLabel == opt.englishName) opt.englishName else "${opt.englishName} (${opt.nativeLabel})"
    }
}

data class AppStrings(
    val navHome: String,
    val navAsk: String,
    val navHistory: String,
    val profileButton: String,
    val timedTestsHeader: String,
    val subjectsHeader: String,
    val subjectsSubtitle: String,
    val uploadHeader: String,
    val askStudyOnlyTitle: String,
    val askWelcome: String,
    val askInputPlaceholder: String,
    val geminiSignInTitle: String,
    val geminiSignInSubtitle: String,
    val geminiSignInButton: String,
    val profileScreenTitle: String,
    val tabMyProfile: String,
    val tabLeaderboard: String,
    val tabFriends: String,
    val selectGradeLabel: String,
    val selectLanguageLabel: String,
    val saveProfileChanges: String,
    val signOutLabel: String
) {
    companion object {
        fun forLanguage(language: String): AppStrings = when (LanguageCatalog.normalize(language)) {
            "Hindi" -> AppStrings(
                navHome = "होम",
                navAsk = "पूछें",
                navHistory = "इतिहास",
                profileButton = "प्रोफ़ाइल",
                timedTestsHeader = "कक्षा-वार परीक्षाएँ",
                subjectsHeader = "विषय चुनें और अभ्यास करें",
                subjectsSubtitle = "अपनी कक्षा के अनुसार विषय चुनें:",
                uploadHeader = "नया प्रश्न जोड़ें",
                askStudyOnlyTitle = "जेमिनी अध्ययन ट्यूटर • बोनस कॉइन",
                askWelcome = "नमस्ते! मैं आपका जेमिनी स्टडी ट्यूटर हूँ। मुझसे केवल किताबों, पढ़ाई और परीक्षाओं से जुड़े सवाल पूछें!",
                askInputPlaceholder = "किताबों, पढ़ाई या टेस्ट के बारे में पूछें...",
                geminiSignInTitle = "Gemini में साइन इन करें",
                geminiSignInSubtitle = "केवल किताबों, पढ़ाई और टेस्ट से जुड़े सवाल पूछने के लिए एक बार Gemini से जुड़ें।",
                geminiSignInButton = "Gemini में साइन इन करें",
                profileScreenTitle = "प्रोफ़ाइल और समुदाय",
                tabMyProfile = "मेरी प्रोफ़ाइल",
                tabLeaderboard = "लीडरबोर्ड",
                tabFriends = "दोस्त",
                selectGradeLabel = "कक्षा / ग्रेड",
                selectLanguageLabel = "पसंदीदा भाषा",
                saveProfileChanges = "प्रोफ़ाइल सहेजें",
                signOutLabel = "साइन आउट"
            )
            "Odia" -> AppStrings(
                navHome = "ହୋମ୍",
                navAsk = "ପଚାରନ୍ତୁ",
                navHistory = "ଇତିହାସ",
                profileButton = "ପ୍ରୋଫାଇଲ୍",
                timedTestsHeader = "ଶ୍ରେଣୀ-ଅନୁଯାୟୀ ପରୀକ୍ଷା",
                subjectsHeader = "ବିଷୟ ବାଛନ୍ତୁ ଏବଂ ଅଭ୍ୟାସ କରନ୍ତୁ",
                subjectsSubtitle = "ଆପଣଙ୍କ ଶ୍ରେଣୀ ଅନୁଯାୟୀ ବିଷୟ ବାଛନ୍ତୁ:",
                uploadHeader = "ନୂଆ ପ୍ରଶ୍ନ ଯୋଡନ୍ତୁ",
                askStudyOnlyTitle = "ଜେମିନି ଅଧ୍ୟୟନ ଟ୍ୟୁଟର୍ • ବୋନସ୍ କଏନ୍",
                askWelcome = "ନମସ୍କାର! ମୁଁ ଆପଣଙ୍କ ଜେମିନି ଅଧ୍ୟୟନ ଟ୍ୟୁଟର୍। କେବଳ ବହି, ପାଠପଢ଼ା ଓ ପରୀକ୍ଷା ବିଷୟରେ ପ୍ରଶ୍ନ ପଚାରନ୍ତୁ!",
                askInputPlaceholder = "ବହି, ପାଠପଢ଼ା କିମ୍ବା ପରୀକ୍ଷା ବିଷୟରେ ପଚାରନ୍ତୁ...",
                geminiSignInTitle = "Gemini ରେ ସାଇନ୍ ଇନ୍ କରନ୍ତୁ",
                geminiSignInSubtitle = "କେବଳ ବହି, ପାଠପଢ଼ା ଏବଂ ପରୀକ୍ଷା ପ୍ରଶ୍ନ ପାଇଁ ଥରେ Gemini ସହିତ ସଂଯୋଗ କରନ୍ତୁ।",
                geminiSignInButton = "Gemini ରେ ସାଇନ୍ ଇନ୍ କରନ୍ତୁ",
                profileScreenTitle = "ପ୍ରୋଫାଇଲ୍ ଏବଂ ସମ୍ପ୍ରଦାୟ",
                tabMyProfile = "ମୋ ପ୍ରୋଫାଇଲ୍",
                tabLeaderboard = "ଲିଡରବୋର୍ଡ",
                tabFriends = "ବନ୍ଧୁମାନେ",
                selectGradeLabel = "ଶ୍ରେଣୀ / ଗ୍ରେଡ୍",
                selectLanguageLabel = "ପସନ୍ଦର ଭାଷା",
                saveProfileChanges = "ପ୍ରୋଫାଇଲ୍ ସେଭ୍ କରନ୍ତୁ",
                signOutLabel = "ସାଇନ୍ ଆଉଟ୍"
            )
            "Bengali" -> AppStrings(
                navHome = "হোম",
                navAsk = "জিজ্ঞাসা",
                navHistory = "ইতিহাস",
                profileButton = "প্রোফাইল",
                timedTestsHeader = "শ্রেণি-ভিত্তিক পরীক্ষা",
                subjectsHeader = "বিষয় বেছে নিয়ে অনুশীলন করুন",
                subjectsSubtitle = "আপনার শ্রেণির জন্য একটি বিষয় নির্বাচন করুন:",
                uploadHeader = "নতুন প্রশ্ন যোগ করুন",
                askStudyOnlyTitle = "জেমিনি স্টাডি টিউটর • বোনাস কয়েন",
                askWelcome = "নমস্কার! আমি আপনার জেমিনি স্টাডি টিউটর। শুধু বই, পড়াশোনা ও পরীক্ষা নিয়ে প্রশ্ন করুন!",
                askInputPlaceholder = "বই, পড়াশোনা বা পরীক্ষা নিয়ে জিজ্ঞাসা করুন...",
                geminiSignInTitle = "Gemini-তে সাইন ইন করুন",
                geminiSignInSubtitle = "বই, পড়াশোনা ও পরীক্ষা সংক্রান্ত প্রশ্নের জন্য একবার Gemini-তে সাইন ইন করুন।",
                geminiSignInButton = "Gemini-তে সাইন ইন করুন",
                profileScreenTitle = "প্রোফাইল ও কমিউনিটি",
                tabMyProfile = "আমার প্রোফাইল",
                tabLeaderboard = "লিডারবোর্ড",
                tabFriends = "বন্ধুরা",
                selectGradeLabel = "শ্রেণি / গ্রেড",
                selectLanguageLabel = "পছন্দের ভাষা",
                saveProfileChanges = "পরিবর্তন সংরক্ষণ করুন",
                signOutLabel = "সাইন আউট"
            )
            "Tamil" -> AppStrings(
                navHome = "முகப்பு",
                navAsk = "கேள்",
                navHistory = "வரலாறு",
                profileButton = "சுயவிவரம்",
                timedTestsHeader = "வகுப்பு வாரியான தேர்வுகள்",
                subjectsHeader = "பாடங்களைத் தேர்ந்தெடுத்து பயிற்சி செய்",
                subjectsSubtitle = "உங்கள் வகுப்பிற்கான பாடத்தைத் தேர்ந்தெடுக்கவும்:",
                uploadHeader = "புதிய கேள்வி சேர்",
                askStudyOnlyTitle = "ஜெமினி கல்வி வழிகாட்டி • போனஸ் நாணயங்கள்",
                askWelcome = "வணக்கம்! நான் உங்கள் ஜெமினி கல்வி வழிகாட்டி. புத்தகங்கள், படிப்பு மற்றும் தேர்வுகள் பற்றி மட்டும் கேளுங்கள்!",
                askInputPlaceholder = "புத்தகங்கள், படிப்பு அல்லது தேர்வு பற்றி கேளுங்கள்...",
                geminiSignInTitle = "Gemini-ல் உள்நுழையவும்",
                geminiSignInSubtitle = "புத்தகங்கள், படிப்பு மற்றும் தேர்வு கேள்விகளுக்கு ஒருமுறை Gemini-ல் உள்நுழையவும்.",
                geminiSignInButton = "Gemini-ல் உள்நுழை",
                profileScreenTitle = "சுயவிவரம் & சமூகம்",
                tabMyProfile = "என் சுயவிவரம்",
                tabLeaderboard = "தரவரிசை",
                tabFriends = "நண்பர்கள்",
                selectGradeLabel = "வகுப்பு / தரம்",
                selectLanguageLabel = "விரும்பிய மொழி",
                saveProfileChanges = "மாற்றங்களைச் சேமி",
                signOutLabel = "வெளியேறு"
            )
            "Telugu" -> AppStrings(
                navHome = "హోమ్",
                navAsk = "అడగండి",
                navHistory = "చరిత్ర",
                profileButton = "ప్రొఫైల్",
                timedTestsHeader = "తరగతి వారీ పరీక్షలు",
                subjectsHeader = "విషయాలను ఎంచుకొని సాధన చేయండి",
                subjectsSubtitle = "మీ తరగతి కోసం ఒక సబ్జెక్ట్ ఎంచుకోండి:",
                uploadHeader = "కొత్త ప్రశ్నను జోడించండి",
                askStudyOnlyTitle = "జెమిని స్టడీ ట్యూటర్ • బోనస్ కాయిన్స్",
                askWelcome = "నమస్కారం! నేను మీ జెమిని స్టడీ ట్యూటర్‌ని. పుస్తకాలు, చదువు మరియు పరీక్షల గురించి మాత్రమే అడగండి!",
                askInputPlaceholder = "పుస్తకాలు, చదువు లేదా పరీక్షల గురించి అడగండి...",
                geminiSignInTitle = "Gemini లో సైన్ ఇన్ చేయండి",
                geminiSignInSubtitle = "పుస్తకాలు, చదువు మరియు పరీక్షల ప్రశ్నల కోసం ఒకసారి Gemini కి కనెక్ట్ అవ్వండి.",
                geminiSignInButton = "Gemini లో సైన్ ఇన్ చేయండి",
                profileScreenTitle = "ప్రొఫైల్ & కమ్యూనిటీ",
                tabMyProfile = "నా ప్రొఫైల్",
                tabLeaderboard = "లీడర్‌బోర్డ్",
                tabFriends = "స్నేహితులు",
                selectGradeLabel = "తరగతి / గ్రేడ్",
                selectLanguageLabel = "భాష",
                saveProfileChanges = "మార్పులను సేవ్ చేయండి",
                signOutLabel = "సైన్ అవుట్"
            )
            "Marathi" -> AppStrings(
                navHome = "होम",
                navAsk = "विचारा",
                navHistory = "इतिहास",
                profileButton = "प्रोफाइल",
                timedTestsHeader = "इयत्तानिहाय चाचण्या",
                subjectsHeader = "विषय निवडा आणि सराव करा",
                subjectsSubtitle = "तुमच्या इयत्तेनुसार विषय निवडा:",
                uploadHeader = "नवीन प्रश्न जोडा",
                askStudyOnlyTitle = "जेमिनी अभ्यास शिक्षक • बोनस नाणी",
                askWelcome = "नमस्कार! मी तुमचा जेमिनी अभ्यास शिक्षक आहे. फक्त पुस्तके, अभ्यास आणि परीक्षांबद्दल प्रश्न विचारा!",
                askInputPlaceholder = "पुस्तके, अभ्यास किंवा चाचणीबद्दल विचारा...",
                geminiSignInTitle = "Gemini मध्ये साइन इन करा",
                geminiSignInSubtitle = "पुस्तके, अभ्यास आणि परीक्षेच्या प्रश्नांसाठी एकदा Gemini शी जोडा.",
                geminiSignInButton = "Gemini मध्ये साइन इन करा",
                profileScreenTitle = "प्रोफाइल आणि समुदाय",
                tabMyProfile = "माझी प्रोफाइल",
                tabLeaderboard = "लीडरबोर्ड",
                tabFriends = "मित्र",
                selectGradeLabel = "इयत्ता / वर्ग",
                selectLanguageLabel = "भाषा निवडा",
                saveProfileChanges = "प्रोफाइल जतन करा",
                signOutLabel = "साइन आउट"
            )
            "Spanish" -> AppStrings(
                navHome = "Inicio",
                navAsk = "Preguntar",
                navHistory = "Historial",
                profileButton = "Perfil",
                timedTestsHeader = "Pruebas por Grado",
                subjectsHeader = "Elige Materias y Practica",
                subjectsSubtitle = "Explora preguntas por materia para tu grado:",
                uploadHeader = "Subir Pregunta y Foto",
                askStudyOnlyTitle = "Tutor Gemini de Estudio • Monedas Extra",
                askWelcome = "¡Hola! Soy tu Tutor Gemini. ¡Pregúntame solo sobre libros, estudios y exámenes!",
                askInputPlaceholder = "Pregunta sobre libros, estudios o pruebas...",
                geminiSignInTitle = "Iniciar sesión en Gemini",
                geminiSignInSubtitle = "Conéctate una vez a Gemini para hacer preguntas sobre libros, estudios y pruebas.",
                geminiSignInButton = "Iniciar sesión en Gemini",
                profileScreenTitle = "Perfil y Comunidad",
                tabMyProfile = "Mi Perfil",
                tabLeaderboard = "Clasificación",
                tabFriends = "Amigos",
                selectGradeLabel = "Grado / Clase",
                selectLanguageLabel = "Idioma Preferido",
                saveProfileChanges = "Guardar Cambios",
                signOutLabel = "Cerrar Sesión"
            )
            "French" -> AppStrings(
                navHome = "Accueil",
                navAsk = "Demander",
                navHistory = "Historique",
                profileButton = "Profil",
                timedTestsHeader = "Tests par Niveau",
                subjectsHeader = "Choisir des Matières et Pratiquer",
                subjectsSubtitle = "Explorez les questions par matière pour votre niveau :",
                uploadHeader = "Ajouter une Question",
                askStudyOnlyTitle = "Tuteur d'Étude Gemini • Pièces Bonus",
                askWelcome = "Bonjour ! Je suis votre tuteur Gemini. Posez-moi uniquement des questions sur les livres, les études et les tests !",
                askInputPlaceholder = "Posez une question sur vos livres, études ou tests...",
                geminiSignInTitle = "Se connecter à Gemini",
                geminiSignInSubtitle = "Connectez Gemini une seule fois pour poser des questions sur les livres, études et tests.",
                geminiSignInButton = "Se connecter à Gemini",
                profileScreenTitle = "Profil et Communauté",
                tabMyProfile = "Mon Profil",
                tabLeaderboard = "Classement",
                tabFriends = "Amis",
                selectGradeLabel = "Classe / Niveau",
                selectLanguageLabel = "Langue Préférée",
                saveProfileChanges = "Enregistrer les Modifications",
                signOutLabel = "Déconnexion"
            )
            else -> AppStrings(
                navHome = "Home",
                navAsk = "Ask",
                navHistory = "History",
                profileButton = "Profile",
                timedTestsHeader = "Class-Wise Timed Tests",
                subjectsHeader = "Choose Subjects & Practice",
                subjectsSubtitle = "Explore untimed questions by subject for your class:",
                uploadHeader = "Upload MCQ & Picture",
                askStudyOnlyTitle = "Gemini Study Tutor • Bonus Coins",
                askWelcome = "Hi! I am your Gemini Study Tutor. Ask me only about books, studies, and tests! Ask 5 study questions/day (+15 coins), 25/week (+75 coins), or 50/month (+100 coins).",
                askInputPlaceholder = "Ask about books, studies, or tests...",
                geminiSignInTitle = "Sign in to Gemini",
                geminiSignInSubtitle = "Connect Gemini once to ask questions about books, studies, and tests. You won't be asked again on this device.",
                geminiSignInButton = "Sign in to Gemini",
                profileScreenTitle = "Profile & Community",
                tabMyProfile = "My Profile",
                tabLeaderboard = "Leaderboard",
                tabFriends = "Friends",
                selectGradeLabel = "Class / Grade",
                selectLanguageLabel = "App & Tutor Language",
                saveProfileChanges = "Save Profile Changes",
                signOutLabel = "Sign Out"
            )
        }
    }
}
