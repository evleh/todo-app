// @ts-check

import { defineConfig, globalIgnores } from "eslint/config";
import js from "@eslint/js";
import pluginVue from 'eslint-plugin-vue';
import tseslint from 'typescript-eslint';

export default defineConfig([
    globalIgnores(["dist/"]),
    js.configs.recommended,
    ...tseslint.configs.recommended,
    // 'flat/recommended': the lower levels miss untyped props (require-prop-types needs
    // strongly-recommended+). Its formatting rules are mostly auto-fixable via `eslint --fix`
    // and there is no formatter yet; once Prettier is added, switch layout rules off via 'no-layout-rules'.
    ...pluginVue.configs['flat/recommended'],
    {
        files: ["**/*.ts", "**/*.vue"],
        languageOptions: {
            parserOptions: {
                parser: '@typescript-eslint/parser'
            }
        },

    },
    {
        files: ["**/*.vue"],
        rules: tseslint.configs.eslintRecommended.rules,
    },

]);
