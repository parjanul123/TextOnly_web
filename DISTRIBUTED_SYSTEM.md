# Sistem Distribuit TextOnly - Web + Android

## 🏗️ Arhitectură

```
┌─────────────────┐         ┌─────────────────┐
│   Web Client    │         │ Android Client  │
│   (Browser)     │         │   (Mobile App)  │
└────────┬────────┘         └────────┬────────┘
         │                           │
         │        HTTPS/WSS          │
         └───────────┬───────────────┘
                     │
         ┌───────────▼────────────┐
         │   Spring Boot Backend  │
         │   (REST + WebSocket)   │
         └───────────┬────────────┘
                     │
         ┌───────────▼────────────┐
         │   PostgreSQL/H2 DB     │
         └────────────────────────┘
```

## 🔌 Endpoint-uri Comune

### Base URL
- **Local Development**: `http://localhost:8080`
- **Production**: `https://textonly.onrender.com`

### REST API

#### Autentificare
- `POST /auth/qr` - Generează token QR (Web)
- `POST /auth/qr/validate` - Validează QR cu telefon (Android)
- `GET /auth/qr/status/{token}` - Status token (Web polling)
- `GET /auth/qr/session?token=xxx` - Session info
- `POST /auth/qr/logout` - Logout

#### Mesaje
- `POST /api/messages/send` - Trimite mesaj
- `GET /api/messages/{phoneNumber}` - Obține mesaje

#### Profil
- `GET /users/profile/{userId}` - Profil utilizator
- `POST /users/profile` - Update profil
- `GET /users/balance/{userId}` - Balanță OnlyCoins

#### Magazin
- `GET /store/items` - Toate itemurile
- `GET /store/items/{type}` - Items pe tip (FRAME, EMOTICON)
- `POST /store/buy` - Cumpără item
- `GET /store/inventory/{userId}` - Inventar utilizator
- `GET /store/transactions/{userId}` - Istoric tranzacții

#### OnlyCoins
- `POST /store/coins/add` - Adaugă coins
- `POST /store/coins/remove` - Scoate coins

#### Cadouri
- `GET /store/gifts` - Lista cadouri
- `POST /store/gift/send` - Trimite cadou

### WebSocket (Real-time)

#### Connection
- **Endpoint**: `/ws`
- **Protocol**: STOMP over WebSocket
- **Fallback**: SockJS

#### Channels
- **Subscribe**: `/topic/messages/{phoneNumber}` - Mesaje noi
- **Subscribe**: `/topic/profile/{userId}` - Update profil
- **Subscribe**: `/topic/gifts/{userId}` - Cadouri primite
- **Send**: `/app/chat.sendMessage` - Trimite mesaj
- **Send**: `/app/profile.update` - Update profil

## 📱 Integrare Android

### 1. Dependințe Gradle
```gradle
// Retrofit pentru REST API
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// OkHttp pentru logging
implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'

// WebSocket
implementation 'org.java-websocket:Java-WebSocket:1.5.3'
// sau
implementation 'com.squareup.okhttp3:okhttp:4.11.0'
```

### 2. API Service (Android)
```kotlin
interface TextOnlyApi {
    @POST("auth/qr/validate")
    suspend fun validateQr(@Body request: QrValidateRequest): Boolean
    
    @GET("api/messages/{phoneNumber}")
    suspend fun getMessages(@Path("phoneNumber") phone: String): List<Message>
    
    @POST("api/messages/send")
    suspend fun sendMessage(@Body message: Message): Message
    
    @GET("users/profile/{userId}")
    suspend fun getProfile(@Path("userId") userId: Long): User
    
    @GET("store/items")
    suspend fun getStoreItems(): List<StoreItem>
    
    @POST("store/buy")
    suspend fun buyItem(@Body request: BuyRequest): BuyResponse
}
```

### 3. Retrofit Setup
```kotlin
object RetrofitClient {
    private const val BASE_URL = "https://textonly.onrender.com/"
    
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    val api: TextOnlyApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TextOnlyApi::class.java)
}
```

### 4. WebSocket Android
```kotlin
class WebSocketManager(private val userId: String) {
    private var stompClient: StompClient? = null
    
    fun connect() {
        stompClient = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP, 
            "wss://textonly.onrender.com/ws"
        )
        
        stompClient?.connect()
        
        // Subscribe to messages
        stompClient?.topic("/topic/messages/$userId")?.subscribe { message ->
            val msg = Gson().fromJson(message.payload, Message::class.java)
            // Handle new message
        }
        
        // Subscribe to gifts
        stompClient?.topic("/topic/gifts/$userId")?.subscribe { gift ->
            // Handle new gift
        }
    }
    
    fun sendMessage(msg: Message) {
        stompClient?.send("/app/chat.sendMessage", Gson().toJson(msg))?.subscribe()
    }
}
```

## 🌐 Integrare Web

### 1. API Client (JavaScript)
```javascript
class TextOnlyAPI {
    constructor(baseURL = 'https://textonly.onrender.com') {
        this.baseURL = baseURL;
    }
    
    async validateQR(token, phoneNumber) {
        const response = await fetch(`${this.baseURL}/auth/qr/validate`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ token, phoneNumber })
        });
        return response.json();
    }
    
    async getMessages(phoneNumber) {
        const response = await fetch(`${this.baseURL}/api/messages/${phoneNumber}`);
        return response.json();
    }
    
    async sendMessage(message) {
        const response = await fetch(`${this.baseURL}/api/messages/send`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(message)
        });
        return response.json();
    }
    
    async getStoreItems() {
        const response = await fetch(`${this.baseURL}/store/items`);
        return response.json();
    }
}

const api = new TextOnlyAPI();
```

### 2. WebSocket Client (Web)
```javascript
class WebSocketClient {
    constructor(userId) {
        this.userId = userId;
        this.stompClient = null;
    }
    
    connect() {
        const socket = new SockJS('https://textonly.onrender.com/ws');
        this.stompClient = Stomp.over(socket);
        
        this.stompClient.connect({}, () => {
            // Subscribe to messages
            this.stompClient.subscribe(`/topic/messages/${this.userId}`, (message) => {
                const msg = JSON.parse(message.body);
                this.handleNewMessage(msg);
            });
            
            // Subscribe to gifts
            this.stompClient.subscribe(`/topic/gifts/${this.userId}`, (gift) => {
                const g = JSON.parse(gift.body);
                this.handleNewGift(g);
            });
        });
    }
    
    sendMessage(message) {
        this.stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(message));
    }
    
    handleNewMessage(msg) {
        // Update UI with new message
    }
    
    handleNewGift(gift) {
        // Show gift notification
    }
}
```

## 🔄 Flow-uri Comune

### 1. Login Flow
```
Web:
1. User accesează /qr-login-demo.html
2. Generează QR code prin POST /auth/qr
3. Polling la GET /auth/qr/status/{token}
4. După validare → redirect la chat

Android:
1. User scanează QR code
2. Trimite POST /auth/qr/validate cu token + phoneNumber
3. Backend validează și marchează session-ul
4. Web primește confirmarea prin polling
```

### 2. Messaging Flow
```
Send:
Web/Android → POST /api/messages/send → Backend → WebSocket broadcast

Receive:
Backend → WebSocket /topic/messages/{userId} → Web/Android
```

### 3. Store Purchase Flow
```
1. Client → GET /store/items (vezi produse)
2. Client → POST /store/buy { userId, itemId }
3. Backend:
   - Verifică balance
   - Deduce coins
   - Adaugă în inventory
   - Creează transaction record
4. Response → { success, newBalance }
```

## 🔐 Securitate

### Headers Necesare
- `Content-Type: application/json`
- `Authorization: Bearer {token}` (viitor - JWT)

### CORS
- Backend acceptă toate originile (`*`)
- Permite credentials
- Expune headers necesare

### WebSocket Auth
- Token în query params: `/ws?token=xxx`
- Sau în headers STOMP

## 📊 Sincronizare Date

### Strategie
1. **REST pentru citire** (GET requests)
2. **WebSocket pentru updates** (real-time)
3. **Fallback polling** dacă WebSocket fail

### Conflict Resolution
- **Last-write-wins** pentru profile updates
- **Timestamp-based** pentru messages
- **Transaction log** pentru OnlyCoins

## 🚀 Deployment

### Backend
```bash
# Local
cd backend
mvn spring-boot:run

# Production
git push heroku main
# sau
render deploy
```

### Web Client
- Hosted static în `/backend/src/main/resources/static/`
- Sau deploy separat pe Vercel/Netlify

### Android APK
- Build în Android Studio
- Upload pe Google Play Store
- Sau distribute .aab direct

## 📝 Checklist Integrare

- [x] Backend API REST complet
- [x] WebSocket pentru real-time
- [x] CORS configurat pentru Android
- [x] API Documentation
- [ ] JWT Authentication (viitor)
- [ ] Rate limiting
- [ ] Data encryption
- [ ] Offline support (Android)
- [ ] Service Worker (Web PWA)

## 🐛 Troubleshooting

### Android nu se conectează
1. Verifică BASE_URL în RetrofitClient
2. Verifică permisiuni INTERNET în manifest
3. Check network_security_config pentru HTTPS

### WebSocket disconnect
1. Implementează reconnection logic
2. Verifică timeout settings
3. Fallback la long-polling

### CORS errors
1. Backend CORS configurat corect?
2. Headers setate în requests?
3. Preflight OPTIONS handled?

## 📚 Resources
- [Spring Boot WebSocket](https://spring.io/guides/gs/messaging-stomp-websocket/)
- [Retrofit Android](https://square.github.io/retrofit/)
- [SockJS Client](https://github.com/sockjs/sockjs-client)
