const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ??
  "http://localhost:8080";

interface RouteContext {
  params: Promise<{
    cartItemId: string;
  }>;
}

async function forwardRequest(
  request: Request,
  method: "PUT" | "DELETE",
  cartItemId: string
) {
  const authorization =
    request.headers.get("authorization");

  if (!authorization) {
    return Response.json(
      {
        message:
          "Authentication token is required.",
      },
      {
        status: 401,
      }
    );
  }

  try {
    const headers: HeadersInit = {
      Accept: "application/json",
      Authorization: authorization,
    };

    let body: string | undefined;

    if (method === "PUT") {
      headers["Content-Type"] =
        "application/json";

      body = await request.text();
    }

    const backendResponse =
      await fetch(
        `${API_BASE_URL}/api/customers/me/cart/items/${cartItemId}`,
        {
          method,
          headers,
          body,
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
          },
          {
            status:
              backendResponse.status,
          }
        );
      }

      return Response.json(
        {
          message:
            `Cart request failed with HTTP ${backendResponse.status}.`,
        },
        {
          status:
            backendResponse.status,
        }
      );
    }

    return new Response(
      responseText,
      {
        status:
          backendResponse.status,

        headers: {
          "Content-Type":
            "application/json",
        },
      }
    );
  } catch (error) {
    console.error(
      "Cart item proxy error:",
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

export async function PUT(
  request: Request,
  context: RouteContext
) {
  const { cartItemId } =
    await context.params;

  return forwardRequest(
    request,
    "PUT",
    cartItemId
  );
}

export async function DELETE(
  request: Request,
  context: RouteContext
) {
  const { cartItemId } =
    await context.params;

  return forwardRequest(
    request,
    "DELETE",
    cartItemId
  );
}