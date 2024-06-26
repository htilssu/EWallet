package com.ewallet.ewallet.service;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.SecretGenerator;
import com.bastiaanjansen.otp.TOTPGenerator;
import lombok.Getter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service
public class OTPService {
    byte[] secret = SecretGenerator.generate();

    @Getter
    TOTPGenerator totp = new TOTPGenerator.Builder(secret)
            .withHOTPGenerator(builder -> {
                builder.withPasswordLength(6);
                builder.withAlgorithm(HMACAlgorithm.SHA512);
            })
            .withPeriod(Duration.ofSeconds(30))
            .build();

    /**
     * Tạo mã OTP mới
     * @return mã otp
     */
    @Async
    public CompletableFuture<String> generateOTP() {
        var otp = totp.now();
        return CompletableFuture.completedFuture(otp);
    }

    /**
     * Xác thực OTP có đúng hay không dựa vào tham số OTP được truyền vào,
     * nếu đúng thì trả về {@code  true}, ngược lại trả về {@code false}
     * @param otpCode mã OTP cần xác thực
     * @return {@code true} nếu mã OTP đúng, ngược lại trả về {@code false}
     */
   public boolean verify(String otpCode){
       return totp.verify(otpCode);
   }
}
