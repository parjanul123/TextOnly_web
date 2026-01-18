# TextOnly - Deployment pe Vercel

## Configurare rapida

### 1. Frontend (Next.js pe Vercel)
```bash
npm install
npm run build
```

### 2. Backend (Java Spring Boot)
Deplasează pe una din aceste platforme:
- **Railway.app** (recomandat - gratuit inițial)
- **Render.com**
- **Heroku** (plată)

### 3. Variabile de mediu în Vercel
Adaugă în Project Settings > Environment Variables:
```
NEXT_PUBLIC_API_URL=https://your-backend-url.com
```

### 4. Conectare la git
```bash
git add .
git commit -m "Vercel deployment setup"
git push origin main
```

Apoi conectează repo-ul la Vercel și deploiază automat.

## Structura pentru Vercel

```
d:\TextOnlyWeb/
├── app/                 # Next.js app directory
├── public/             # Static files
├── package.json        # Frontend dependencies
├── next.config.js      # Next.js config
├── vercel.json         # Vercel config
├── backend/            # Java Spring Boot (deploy separat)
└── docker-compose.yml  # Development locally
```

## Deploy Backend separat

### Railway.app
1. Conectează repo-ul GitHub
2. Selectează branch: `main`
3. Railway va detecta `pom.xml` și va deploy automat

### Sau Dockerfile
```bash
docker build -t textonly-backend ./backend
docker tag textonly-backend your-registry/textonly-backend:latest
docker push your-registry/textonly-backend:latest
```

## Local testing
```bash
docker-compose up -d
npm run dev
```
