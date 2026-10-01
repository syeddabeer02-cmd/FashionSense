const API_BASE_URL =
  process.env.BACKEND_API_BASE_URL ??
  "http://localhost:8080";

export async function POST(request: Request) {
  try {
    const requestBody = await request.text();

    const backendResponse = await fetch(
      `${API_BASE_URL}/api/auth/login`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
        body: requestBody,
        cache: "no-store",
      }
    );

    const responseText =
      await backendResponse.text();

    if (!responseText) {
      return Response.json(
        {
          message:
            backendResponse.ok
              ? "Backend returned an empty login response."
              : `Login failed with HTTP ${backendResponse.status}.`,
        },
        {
          status: backendResponse.ok
            ? 502
            : backendResponse.status,
        }
      );
    }

    return new Response(responseText, {
      status: backendResponse.status,
      headers: {
        "Content-Type": "application/json",
      },
    });
  } catch (error) {
    console.error("Login proxy error:", error);

    return Response.json(
      {
        message:
          "Could not connect to the Fashion Sense backend.",
      },
      {
        status: 503,
      }
    );
  }
}