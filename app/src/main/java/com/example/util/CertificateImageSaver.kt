package com.example.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object CertificateImageSaver {

    fun renderCertificateBitmap(
        context: Context,
        studentName: String,
        schoolName: String,
        certificateTitle: String,
        achievementLine: String,
        dateText: String
    ): Bitmap {
        val width = 1400
        val height = 1760
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Warm Ivory background
        canvas.drawColor(Color.parseColor("#FFFDF5"))

        val outerRect = RectF(44f, 44f, width - 44f, height - 44f)
        val innerRect = RectF(68f, 68f, width - 68f, height - 68f)

        val outerBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 10f
            color = Color.parseColor("#F59E0B")
        }
        canvas.drawRoundRect(outerRect, 48f, 48f, outerBorderPaint)

        val innerBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = Color.parseColor("#FBBF24")
        }
        canvas.drawRoundRect(innerRect, 36f, 36f, innerBorderPaint)

        var y = 140f

        // Certificate gold seal
        val rawSeal = BitmapFactory.decodeResource(context.resources, R.drawable.img_certificate_seal)
        if (rawSeal != null) {
            val sealSize = 240f
            val sealLeft = (width - sealSize) / 2f
            val sealRect = RectF(sealLeft, y, sealLeft + sealSize, y + sealSize)
            canvas.save()
            val clipPath = Path().apply {
                addOval(sealRect, Path.Direction.CW)
            }
            canvas.clipPath(clipPath)
            canvas.drawBitmap(rawSeal, null, sealRect, Paint(Paint.FILTER_BITMAP_FLAG))
            canvas.restore()
            rawSeal.recycle()
            y += sealSize + 48f
        } else {
            y += 60f
        }

        // Header label
        val headerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#78350F")
            textSize = 36f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            letterSpacing = 0.14f
        }
        y += drawCenteredTextBlock(
            canvas = canvas,
            text = "CERTIFICATE OF ACHIEVEMENT",
            paint = headerPaint,
            maxWidth = width - 180,
            startY = y
        ) + 28f

        // Certificate Title
        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#1E293B")
            textSize = 62f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        y += drawCenteredTextBlock(
            canvas = canvas,
            text = certificateTitle,
            paint = titlePaint,
            maxWidth = width - 180,
            startY = y
        ) + 56f

        // "Proudly awarded to"
        val subtitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#475569")
            textSize = 38f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }
        y += drawCenteredTextBlock(
            canvas = canvas,
            text = "Proudly awarded to",
            paint = subtitlePaint,
            maxWidth = width - 180,
            startY = y
        ) + 24f

        // Student Name
        val namePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#1F6AEA")
            textSize = 76f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        y += drawCenteredTextBlock(
            canvas = canvas,
            text = studentName.ifBlank { "BrightQuiz Student" },
            paint = namePaint,
            maxWidth = width - 180,
            startY = y
        ) + 20f

        // School Name
        val schoolPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#334155")
            textSize = 44f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        y += drawCenteredTextBlock(
            canvas = canvas,
            text = schoolName.ifBlank { "BrightQuiz Academy" },
            paint = schoolPaint,
            maxWidth = width - 180,
            startY = y
        ) + 64f

        // Achievement highlight box
        val achievementPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#78350F")
            textSize = 40f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        val achievementLayout = createCenteredLayout(achievementLine, achievementPaint, width - 260)
        val boxPadV = 40f
        val boxRect = RectF(
            110f,
            y,
            width - 110f,
            y + achievementLayout.height + boxPadV * 2f
        )
        val boxFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FEF3C7")
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(boxRect, 30f, 30f, boxFillPaint)

        canvas.save()
        canvas.translate(130f, y + boxPadV)
        achievementLayout.draw(canvas)
        canvas.restore()

        // Footer near bottom
        val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#64748B")
            textSize = 32f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }
        drawCenteredTextBlock(
            canvas = canvas,
            text = "Issued on $dateText  •  BrightQuiz Academy",
            paint = footerPaint,
            maxWidth = width - 180,
            startY = height - 180f
        )

        return bitmap
    }

    suspend fun saveCertificateToGallery(
        context: Context,
        studentName: String,
        schoolName: String,
        certificateTitle: String,
        achievementLine: String,
        dateText: String
    ): Result<Uri> = withContext(Dispatchers.IO) {
        var bitmap: Bitmap? = null
        try {
            bitmap = renderCertificateBitmap(
                context = context,
                studentName = studentName,
                schoolName = schoolName,
                certificateTitle = certificateTitle,
                achievementLine = achievementLine,
                dateText = dateText
            )
            val pngBytes = ByteArrayOutputStream().use { baos ->
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)) {
                    "Failed to encode certificate image."
                }
                baos.toByteArray()
            }

            val fileName = "BrightQuiz_Certificate_${System.currentTimeMillis()}.png"
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        "${Environment.DIRECTORY_PICTURES}/BrightQuiz"
                    )
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(collection, values)
                ?: return@withContext Result.failure(
                    IllegalStateException("Could not create gallery entry on this device.")
                )

            try {
                resolver.openOutputStream(uri)?.use { out ->
                    out.write(pngBytes)
                    out.flush()
                } ?: throw IllegalStateException("Unable to open gallery output stream.")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val publishValues = ContentValues().apply {
                        put(MediaStore.Images.Media.IS_PENDING, 0)
                    }
                    resolver.update(uri, publishValues, null, null)
                }
                Result.success(uri)
            } catch (e: Exception) {
                runCatching { resolver.delete(uri, null, null) }
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            bitmap?.recycle()
        }
    }

    suspend fun writeCertificatePngToUri(
        context: Context,
        targetUri: Uri,
        studentName: String,
        schoolName: String,
        certificateTitle: String,
        achievementLine: String,
        dateText: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        var bitmap: Bitmap? = null
        try {
            bitmap = renderCertificateBitmap(
                context = context,
                studentName = studentName,
                schoolName = schoolName,
                certificateTitle = certificateTitle,
                achievementLine = achievementLine,
                dateText = dateText
            )
            context.contentResolver.openOutputStream(targetUri)?.use { out ->
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                    "Failed to write certificate image."
                }
                out.flush()
            } ?: return@withContext Result.failure(IllegalStateException("Unable to write to file."))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            bitmap?.recycle()
        }
    }

    private fun createCenteredLayout(text: String, paint: TextPaint, width: Int): StaticLayout {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setIncludePad(false)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(text, paint, width, Layout.Alignment.ALIGN_CENTER, 1.0f, 0f, false)
        }
    }

    private fun drawCenteredTextBlock(
        canvas: Canvas,
        text: String,
        paint: TextPaint,
        maxWidth: Int,
        startY: Float
    ): Float {
        val layout = createCenteredLayout(text, paint, maxWidth)
        val startX = (canvas.width - maxWidth) / 2f
        canvas.save()
        canvas.translate(startX, startY)
        layout.draw(canvas)
        canvas.restore()
        return layout.height.toFloat()
    }
}
