package ru.qwonix.android.foxwhiskers.fragment

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentQrBinding

class QrFragment : Fragment(R.layout.fragment_qr) {

    private val TAG = "QrFragment"

    companion object {
        fun newInstance() = OrderConfirmationFragment()
    }

    private val args: QrFragmentArgs by navArgs()

    private lateinit var binding: FragmentQrBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val text = args.qrCodeData// The text you want to encode

        val width = 500 // Width of the QR code
        val height = 500 // Height of the QR code

        try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height)
            val bitMatrixWidth = bitMatrix.width
            val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixWidth, Bitmap.Config.RGB_565)
            for (x in 0 until bitMatrixWidth) {
                for (y in 0 until bitMatrixWidth) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            binding.qrCodeImageView.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            Log.e(TAG, "code generation error", e)
        }
    }
}