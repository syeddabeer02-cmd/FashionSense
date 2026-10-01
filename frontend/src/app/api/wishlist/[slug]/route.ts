const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ??
  "http://localhost:8080";

interface RouteContext {
  params: Promise<{
    slug: string;
  }>;
}

async function forwardWishlistRequest(
  request: Request,
  method: "PUT" | "DELETE",
  slug: string
) {
  const authorization =
    request.headers.get(
      "authorization"
    );

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
    const backendResponse =
      await fetch(
        `${API_BASE_URL}/api/customers/me/wishlist/${slug}`,
        {
          method,

          headers: {
            Accept:
              "application/json",

            Authorization:
              authorization,
          },

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
            `Wishlist request failed with HTTP ${backendResponse.status}.`,
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
      "Wishlist item proxy error:",
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
  const { slug } =
    await context.params;

  return forwardWishlistRequest(
    request,
    "PUT",
    slug
  );
}

export async function DELETE(
  request: Request,
  context: RouteContext
) {
  const { slug } =
    await context.params;

  return forwardWishlistRequest(
    request,
    "DELETE",
    slug
  );
}