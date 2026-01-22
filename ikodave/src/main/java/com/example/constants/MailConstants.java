package com.example.constants;

public class MailConstants {

    public static final String SUBJECT = """
                Verification | Ikodave
            """;

    public static final String TEXT = """
                Welcome to Ikodave!
                
                Please verify your email by visiting the link below: 
                %s
                
                If the link doesn’t work, copy & paste it into your browser.
            """;


    public static final String HTML = """
                <html>
                    <body style="font-family:Arial,sans-serif; line-height:1.5;">
                      <h2>Hello %s, Welcome to Ikodave!</h2>
                      <p>Click the button below to verify your email address:</p>
                      <p style="text-align:center;">
                        <a href="%s"
                           style="
                             display:inline-block;
                             padding:12px 24px;
                             font-size:16px;
                             color:#ffffff;
                             background-color:#28a745;
                             text-decoration:none;
                             border-radius:4px;
                           ">
                          Verify
                        </a>
                      </p>
                      <p>If that doesn’t work, copy & paste this URL into your browser:</p>
                      <p><a href="%s">%s</a></p>
                      <p>Thank you!<br/>– Ikodave Team</p>
                    </body>
                </html>
            """;


    public static final String smtpHost    = "smtp.gmail.com";
    public static final int    smtpPort    = 587;
    public static final String username    = System.getenv("MAIL_USERNAME");
    public static final String appPassword = System.getenv("MAIL_PASSWORD");
    public static final String fromAddress = System.getenv("MAIL_USERNAME");

}
