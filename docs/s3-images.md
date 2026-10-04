# Optional S3 product-image uploads

Existing product image URLs continue to work. An optional admin-only endpoint
uploads product images using AWS SDK for Java v2. AWS access occurs on the
backend, not in browser JavaScript. It uses the standard SDK credentials chain.

Set these variables in the ignored infrastructure/.env file when a bucket and
scoped credentials or instance role are available:

```text
S3_IMAGES_ENABLED=true
S3_IMAGES_BUCKET=YOUR_BUCKET
AWS_REGION=YOUR_REGION
S3_IMAGES_PUBLIC_BASE_URL=https://YOUR_IMAGE_CDN_OR_BUCKET_HOST
```

The serving URL must use HTTPS. Prefer a private S3 bucket served through a
CloudFront origin-access configuration. The deployment identity needs s3:PutObject
only under YOUR_BUCKET/products/* for this API; do not grant account-wide access.
For local containers, credential environment variables are passed through from
.env. On an AWS host, prefer a properly configured instance/task role. Keep keys
out of commits and browser responses.

Upload a real PNG, JPEG or WebP with an ADMIN Bearer token:

```powershell
curl.exe -X POST http://localhost:8080/api/product-images/upload -H "Authorization: Bearer $env:ADMIN_TOKEN" -F "productId=1" -F "file=@D:\Images\product.png"
```

For the hosted overlay's backend tunnel/local port, use 18081 instead of 8080.
The response contains objectKey, imageUrl, contentType and sizeBytes. Save the
returned imageUrl through the existing POST /api/product-images JSON endpoint
with productId, altText, displayOrder and primaryImage. This is a staged upload:
object storage and relational metadata are separate operations. Deleting the
image metadata does not delete the object. Bucket lifecycle/cleanup should account
for unattached uploads after unsuccessful metadata operations.

The API checks that the product exists, accepts at most 5 MiB, allows only the
three image MIME types, and compares their magic bytes. It does not fully decode
or transform images. SVG and HTML are rejected. Generated UUID object names
ignore user filenames and avoid path traversal and accidental overwrites.
The endpoint is absent when disabled. Unit tests mock storage calls and need no
AWS account; real upload, permissions and serving URL validation remain pending.
There is currently no storefront admin-upload screen; this is an admin API.

Reference: https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials-chain.html
