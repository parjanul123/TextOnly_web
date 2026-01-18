export async function GET(request: Request) {
  const apiUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
  const path = new URL(request.url).pathname.replace('/api/proxy/', '');

  try {
    const response = await fetch(`${apiUrl}/${path}`, {
      method: request.method,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    const data = await response.json();
    return Response.json(data, { status: response.status });
  } catch (error) {
    return Response.json(
      { error: 'Failed to fetch from backend' },
      { status: 500 }
    );
  }
}

export async function POST(request: Request) {
  const apiUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
  const path = new URL(request.url).pathname.replace('/api/proxy/', '');
  const body = await request.json();

  try {
    const response = await fetch(`${apiUrl}/${path}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
    });

    const data = await response.json();
    return Response.json(data, { status: response.status });
  } catch (error) {
    return Response.json(
      { error: 'Failed to fetch from backend' },
      { status: 500 }
    );
  }
}
