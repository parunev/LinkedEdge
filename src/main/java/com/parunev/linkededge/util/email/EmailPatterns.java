package com.parunev.linkededge.util.email;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailPatterns {

    public static String confirmationEmail(String name, String link) {
        return "<div style=\"font-family: Helvetica, Arial, sans-serif; font-size: 16px; margin: 0; color: #b0c0c\">\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse: collapse; min-width: 100%; width: 100% !important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody>\n" +
                "      <tr>\n" +
                "        <td width=\"100%\" height=\"53\" bgcolor=\"3399FF\">\n" +
                "          <table role=\"presentation\" width=\"100%\" style=\"border-collapse: collapse; max-width: 580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "            <tbody>\n" +
                "              <tr>\n" +
                "                <td width=\"70\" bgcolor=\"#3399FF\" valign=\"middle\">\n" +
                "                  <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse: collapse\">\n" +
                "                    <tbody>\n" +
                "                      <tr>\n" +
                "                        <td style=\"padding-left: 10px\"></td>\n" +
                "                        <td style=\"font-size: 28px; line-height: 1.315789474; Margin-top: 4px; padding-left: 10px\">\n" +
                "                          <span style=\"font-family: Arial, Arial, sans-serif; font-weight: 100; color: #FFFF; text-decoration: none; vertical-align: top; display: inline-block\">Welcome to LinkedEdge!</span>\n" +
                "                        </td>\n" +
                "                      </tr>\n" +
                "                    </tbody>\n" +
                "                  </table>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "            </tbody>\n" +
                "          </table>\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </tbody>\n" +
                "  </table>\n" +
                " <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse: collapse; max-width: 580px; width: 100% !important\" width=\"100%\">\n" +
                "    <tbody>\n" +
                "      <tr>\n" +
                "        <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "        <td>\n" +
                "          <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse: collapse\">\n" +
                "            <tbody>\n" +
                "              <tr>\n" +
                "                <td bgcolor=\"#0370B8\" width=\"100%\" height=\"10\"></td>\n" +
                "              </tr>\n" +
                "            </tbody>\n" +
                "          </table>\n" +
                "        </td>\n" +
                "        <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "      </tr>\n" +
                "    </tbody>\n" +
                "  </table>\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse: collapse; max-width: 580px; width: 100% !important\" width=\"100%\">\n" +
                "    <tbody>\n" +
                "      <tr>\n" +
                "        <td height=\"30\"><br></td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "        <td style=\"font-family: Helvetica, Arial, sans-serif; font-size: 19px; line-height: 1.315789474; max-width: 560px\">\n" +
                "          <p style=\"Margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #0b0c0c\">Dear <b>" + name + "</b>,</p>\n" +
                "          <p style=\"Margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #0b0c0c\">Thank you for joining <b>LinkedEdge</b>, your AI Interview assistant for generating interview questions via LinkedIn. We're thrilled to have you as part of our community.</p>\n" +
                "          <p style=\"Margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #0b0c0c\">To get started with and enhance your application experience, please verify your email address:</p>\n" +
                "          <blockquote style=\"Margin: 0 0 20px 0; border-left: 10px solid #3399FF; padding: 15px 0 0.1px 15px; font-size: 19px; line-height: 25px\">\n" +
                "            <p style=\"Margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #1b1c1c\">\n" +
                "              <a href=\"" + link + "\">Verify Email</a>\n" +
                "            </p>\n" +
                "          </blockquote>\n" +
                "          <p style=\"color: #000;\">The verification link will expire in <b>24 hours</b>.</p>\n" +
                "          <p style=\"color: #000;\">Best regards,<br>\n" +
                "            <b>The LinkedEdge Team</b></p>\n" +
                "        </td>\n" +
                "        <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <td height=\"30\"><br></td>\n" +
                "      </tr>\n" +
                "    </tbody>\n" +
                "  </table>\n" +
                "  <div class=\"yj6qo\"></div>\n" +
                "  <div class=\"adL\">\n" +
                "  </div>\n" +
                "</div>\n";
    }

    public static String changeEmailAddress(String name, String link){
        return "<div style=\"font-family: Helvetica, Arial, sans-serif; font-size: 16px; margin: 0; color: #b0c0c\">\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse: collapse; min-width: 100%; width: 100% !important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody>\n" +
                "      <tr>\n" +
                "        <td width=\"100%\" height=\"53\" bgcolor=\"3399FF\">\n" +
                "          <table role=\"presentation\" width=\"100%\" style=\"border-collapse: collapse; max-width: 580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "            <tbody>\n" +
                "              <tr>\n" +
                "                <td width=\"70\" bgcolor=\"#3399FF\" valign=\"middle\">\n" +
                "                  <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse: collapse\">\n" +
                "                    <tbody>\n" +
                "                      <tr>\n" +
                "                        <td style=\"padding-left: 10px\"></td>\n" +
                "                        <td style=\"font-size: 28px; line-height: 1.315789474; Margin-top: 4px; padding-left: 10px\">\n" +
                "                          <span style=\"font-family: Arial, Arial, sans-serif; font-weight: 100; color: #FFFF; text-decoration: none; vertical-align: top; display: inline-block\">Change Email Address</span>\n" +
                "                        </td>\n" +
                "                      </tr>\n" +
                "                    </tbody>\n" +
                "                  </table>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "            </tbody>\n" +
                "          </table>\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </tbody>\n" +
                "  </table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse: collapse; max-width: 580px; width: 100% !important\" width=\"100%\">\n" +
                "    <tbody>\n" +
                "      <tr>\n" +
                "        <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "        <td>\n" +
                "          <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse: collapse\">\n" +
                "            <tbody>\n" +
                "              <tr>\n" +
                "                <td bgcolor=\"#0370B8\" width=\"100%\" height=\"10\"></td>\n" +
                "              </tr>\n" +
                "            </tbody>\n" +
                "          </table>\n" +
                "        </td>\n" +
                "        <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "      </tr>\n" +
                "    </tbody>\n" +
                "  </table>\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse: collapse; max-width: 580px; width: 100% !important\" width=\"100%\">\n" +
                "    <tbody>\n" +
                "      <tr>\n" +
                "        <td height=\"30\"><br></td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "        <td style=\"font-family: Helvetica, Arial, sans-serif; font-size: 19px; line-height: 1.315789474; max-width: 560px\">\n" +
                "          <p style=\"Margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #0b0c0c\">Dear <b>" + name + "</b>,</p>\n" +
                "          <p style=\"Margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #0b0c0c\">You've requested to change your email address for your <b>LinkedEdge</b> account. Please follow the instructions below to complete the process:</p>\n" +
                "          <blockquote style=\"Margin: 0 0 20px 0; border-left: 10px solid #3399FF; padding: 15px 0 0.1px 15px; font-size: 19px; line-height: 25px\">\n" +
                "            <p style=\"Margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #1b1c1c\">\n" +
                "              <a href=\"" + link + "\">Change Email Address</a>\n" +
                "            </p>\n" +
                "          </blockquote>\n" +
                "          <p style=\"color: #000;\">This link will expire in <b>15 minutes</b>.</p>\n" +
                "          <p style=\"color: #000;\">If you didn't request this change, please ignore this email.</p>\n" +
                "          <p style=\"color: #000;\">Best regards,<br>\n" +
                "            <b>The LinkedEdge Team</b></p>\n" +
                "        </td>\n" +
                "        <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <td height=\"30\"><br></td>\n" +
                "      </tr>\n" +
                "    </tbody>\n" +
                "  </table>\n" +
                "  <div class=\"yj6qo\"></div>\n" +
                "  <div class=\"adL\">\n" +
                "  </div>\n" +
                "</div>\n";
    }

    public static String forgotPasswordEmail(String name, String link) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            font-size: 16px;\n" +
                "            margin: 0;\n" +
                "            color: #1b1c1c;\n" +
                "        }\n" +
                "\n" +
                "        .email-container {\n" +
                "            border-collapse: collapse;\n" +
                "            min-width: 100%;\n" +
                "            width: 100% !important;\n" +
                "        }\n" +
                "\n" +
                "        .header {\n" +
                "            background-color: #3399FF;\n" +
                "        }\n" +
                "\n" +
                "        .header-content {\n" +
                "            max-width: 580px;\n" +
                "            padding-left: 10px;\n" +
                "            padding-right: 10px;\n" +
                "        }\n" +
                "\n" +
                "        .header-title {\n" +
                "            font-size: 28px;\n" +
                "            line-height: 1.315789474;\n" +
                "            margin-top: 4px;\n" +
                "            padding-left: 10px;\n" +
                "            font-weight: 100;\n" +
                "            color: #FFFFFF;\n" +
                "        }\n" +
                "\n" +
                "        .divider {\n" +
                "            background-color: #0370B8;\n" +
                "        }\n" +
                "\n" +
                "        .content-container {\n" +
                "            max-width: 580px;\n" +
                "            width: 100% !important;\n" +
                "            margin-top: 30px;\n" +
                "        }\n" +
                "\n" +
                "        .content-text {\n" +
                "            font-family: Helvetica, Arial, sans-serif;\n" +
                "            font-size: 19px;\n" +
                "            line-height: 1.315789474;\n" +
                "            max-width: 560px;\n" +
                "        }\n" +
                "\n" +
                "        .content-text p {\n" +
                "            margin: 0 0 20px 0;\n" +
                "            font-size: 19px;\n" +
                "            line-height: 25px;\n" +
                "        }\n" +
                "\n" +
                "        .verification-link {\n" +
                "            border-left: 10px solid #3399FF;\n" +
                "            padding: 15px 0 0.1px 15px;\n" +
                "            font-size: 19px;\n" +
                "            line-height: 25px;\n" +
                "        }\n" +
                "\n" +
                "        .verification-link a {\n" +
                "            color: #1b1c1c;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<table class=\"email-container\" role=\"presentation\" width=\"100%\">\n" +
                "    <tbody>\n" +
                "    <tr>\n" +
                "        <td class=\"header\" height=\"53\">\n" +
                "            <table class=\"header-content\" role=\"presentation\" width=\"100%\">\n" +
                "                <tbody>\n" +
                "                <tr>\n" +
                "                    <td width=\"70\" class=\"header-title\" valign=\"middle\">\n" +
                "                        <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"\n" +
                "                               style=\"border-collapse: collapse\">\n" +
                "                            <tbody>\n" +
                "                            <tr>\n" +
                "                                <td style=\"padding-left: 10px\"></td>\n" +
                "                                <td>\n" +
                "                                    <span>Welcome to LinkedEdge!</span>\n" +
                "                                </td>\n" +
                "                            </tr>\n" +
                "                            </tbody>\n" +
                "                        </table>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "                </tbody>\n" +
                "            </table>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "    </tbody>\n" +
                "</table>\n" +
                "<table role=\"presentation\" class=\"divider\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"\n" +
                "       style=\"border-collapse: collapse; max-width: 580px; width: 100% !important\">\n" +
                "    <tbody>\n" +
                "    <tr>\n" +
                "        <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "        <td>\n" +
                "            <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"\n" +
                "                   style=\"border-collapse: collapse\">\n" +
                "                <tbody>\n" +
                "                <tr>\n" +
                "                    <td bgcolor=\"#0370B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                </tr>\n" +
                "                </tbody>\n" +
                "            </table>\n" +
                "        </td>\n" +
                "        <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "    </tbody>\n" +
                "</table>\n" +
                "<table role=\"presentation\" class=\"content-container\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"\n" +
                "       style=\"border-collapse: collapse; max-width: 580px; width: 100% !important\">\n" +
                "    <tbody>\n" +
                "    <tr>\n" +
                "        <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "        <td class=\"content-text\">\n" +
                "            <p>Dear <b>" + name + "</b>,</p>\n" +
                "            <p>You have requested to reset your <b>LinkedEdge</b> account password. We're here to help you regain access to your account.</p>\n" +
                "            <p>To reset your password, please click the link below:</p>\n" +
                "            <blockquote class=\"verification-link\">\n" +
                "                <p>\n" +
                "                    <a href=\"" + link + "\">Reset Password</a>\n" +
                "                </p>\n" +
                "            </blockquote>\n" +
                "            <p>If you did not request this change, please ignore this email, and your password will remain unchanged.</p>\n" +
                "            <p>This password reset link will expire in 24 hours for security reasons.</p>\n" +
                "            <p>Best regards,<br>\n" +
                "                <b>The LinkedEdge Team</b></p>\n" +
                "        </td>\n" +
                "        <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    </tbody>\n" +
                "</table>\n" +
                "</body>\n" +
                "</html>";
    }
}
