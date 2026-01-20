import com.example.models.Menu
import kotlinx.serialization.Serializable

@Serializable
data class MenuListResponse(
    val menus: List<Menu>
)