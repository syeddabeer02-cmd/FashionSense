const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ??
  "http://localhost:8080";

export async function POST(request: Request) {
  try {
    const requestBody = await request.text();

    const backendResponse = await fetch(
      `${API_BASE_URL}/api/auth/verify-email`,
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
      if (backendResponse.ok) {
        return Response.json(
          {
            success: true,
            message:
              "Email verification completed successfully.",
          },
          {
            status: backendResponse.status,
          }
        );
      }

      return Response.json(
        {
          message:
            `Email verification failed with HTTP ${backendResponse.status}.`,
        },
        {
          status: backendResponse.status,
        }
      );
    }

    return new Response(
      responseText,
      {
        status: backendResponse.status,

        headers: {
          "Content-Type":
            "application/json",
        },
      }
    );
  } catch (error) {
    console.error(
      "Email verification proxy error:",
      error
    );

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