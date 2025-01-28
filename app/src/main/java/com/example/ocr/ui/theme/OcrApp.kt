import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ocr.ui.OcrScreen
import com.example.ocr.ui.theme.HomeScreen

@Composable
fun OcrApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "homeScreen") {
        composable("homeScreen") {
            HomeScreen(navController = navController)
        }
        composable("ocrScreen") {
            OcrScreen(navController = navController)
        }
    }
}
