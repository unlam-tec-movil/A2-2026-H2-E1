package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.TuiterApi
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.CreatePostRequest
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.interfaces.PostApi
import ar.edu.unlam.mobile.movil22026h2.data.mappers.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

private val initialPosts =
    listOf(
        Post(
            id = 1,
            authorName = "Ana Gomez",
            authorAvatarUrl = "https://i.pravatar.cc/150?img=1",
            content = "Arrancando la semana con todo.",
        ),
        Post(
            id = 2,
            authorName = "Beto Diaz",
            authorAvatarUrl = "https://i.pravatar.cc/150?img=12",
            content = "Probando la nueva app de la facu.",
        ),
        Post(
            id = 3,
            authorName = "Carla Ruiz",
            authorAvatarUrl = "https://i.pravatar.cc/150?img=32",
            content = "¿Alguien para estudiar para el parcial?",
        ),
        Post(
            id = 4,
            authorName = "Ana Gomez",
            authorAvatarUrl = "https://i.pravatar.cc/150?img=1",
            content = "Otro post de Ana: acá ya debería decir que es favorita.",
        ),
    )

@Singleton
class RemotePostRepositoryImpl
    @Inject
    constructor(
    private val api: TuiterApi,
    private val profileRepository: ProfileRepository,
    private val postApi: PostApi,
) : PostRepository {
    private val postsFlow = MutableStateFlow(initialPosts)

    override fun observePosts(page: Int): Flow<List<Post>> = postsFlow.asStateFlow()

    override suspend fun createPost(content: String) {
        val trimmedContent = content.trim()
        require(trimmedContent.isNotEmpty()) { "El contenido del post no puede estar vacío" }

        api.createPost(CreatePostRequest(message = trimmedContent))

        val profile = runCatching { profileRepository.getProfile() }.getOrNull()
        val nextId = (postsFlow.value.maxOfOrNull { it.id } ?: 0) + 1
        val newPost =
            Post(
                id = nextId,
                authorName = profile?.name?.takeIf { it.isNotBlank() } ?: "Yo",
                authorAvatarUrl = profile?.avatarUrl ?: "",
                content = trimmedContent,
            )

        postsFlow.update { currentPosts -> listOf(newPost) + currentPosts }
    }

    // HU-09: trae el post completo que se abrió desde el feed.
    override suspend fun getPost(id: Int): Post {
        val response = postApi.getPost(id)
        if (!response.isSuccessful) throw HttpException(response)
        return response.body()?.toDomain()
            ?: throw IllegalStateException("La API no devolvió el post $id")
    }

    // HU-09: trae las respuestas del post.
    override suspend fun getReplies(id: Int): List<Post> {
        val response = postApi.getReplies(id)
        if (!response.isSuccessful) throw HttpException(response)
        return response.body().orEmpty().map { it.toDomain() }
    }
}
